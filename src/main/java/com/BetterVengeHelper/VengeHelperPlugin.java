package com.BetterVengeHelper;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "Better Venge Helper"
)
public class VengeHelperPlugin extends Plugin
{
	private static final int SPELLBOOK_VARBIT = 4070;
	private static final int ARCEUUS_SPELLBOOK = 3;
	private static final int LUNAR_SPELLBOOK = 2;
	private static final String SPELL_TARGET_REGEX = "<col=00ff00>(Vengeance|Vengeance Other)</col>.*";
	private static final Pattern SPELL_TARGET_PATTERN = Pattern.compile(SPELL_TARGET_REGEX);
	private static final Set<Integer> activeSpellSpriteIds = new HashSet<>(Arrays.asList(
//			1960, //venge other
			1961 //venge self
	));

	private static int vengeCooldownDuration = 30;
	private Instant lastVengeanceCast;
	private boolean alreadyNotified = false;
	private boolean alreadyCleared = false;


	@Inject
	private Client client;

	@Inject
	private VengeHelperConfig config;
	@Inject
	private com.portaguy.VengeHelperOverlay overlay;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private Notifier notifier;
	@Inject
	KeyManager keyManager;

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(hideReminderHotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		keyManager.unregisterKeyListener(hideReminderHotkeyListener);
	}

	private final HotkeyListener hideReminderHotkeyListener = new HotkeyListener(() -> config.hideReminderHotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			overlayManager.remove(overlay);
			lastVengeanceCast = null;
		}
	};


	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (lastVengeanceCast != null){
			if (client.getVarbitValue(SPELLBOOK_VARBIT) == LUNAR_SPELLBOOK) {

				final Duration vengeCooldown = Duration.ofSeconds(vengeCooldownDuration);
				final Duration sinceVengeCast = Duration.between(lastVengeanceCast, Instant.now());

				if (sinceVengeCast.compareTo(vengeCooldown) >= 0 && !alreadyNotified)
				{
					overlayManager.add(overlay);
					notifier.notify("You need to cast Vengeance!");
					alreadyNotified = true;

				}
			}
			// We don't need to be on lunar spellbook to clear the reminder
			final Duration reminderClearDuration = Duration.ofSeconds(config.vengeReminderTimeoutSeconds() + vengeCooldownDuration);
			final Duration sinceVengeCast = Duration.between(lastVengeanceCast, Instant.now());

			if (sinceVengeCast.compareTo(reminderClearDuration) >= 0 && !alreadyCleared) {
				overlayManager.remove(overlay);
				alreadyCleared = true;


			}
		}
	}


	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Check the menu option clicked is one of the resurrection spells
		Matcher matcher = SPELL_TARGET_PATTERN.matcher(event.getMenuTarget());
		if (!matcher.matches())
		{
			return;
		}
		Widget widget = event.getWidget();
		if (widget == null)
		{
			return;
		}
		// In the 30-second cool down where the spell can't recast the opacity changes from 0 to 150
		if (activeSpellSpriteIds.contains(widget.getSpriteId()) && widget.getOpacity() == 0)
		{
			lastVengeanceCast = Instant.now();
			overlayManager.remove(overlay);
			alreadyNotified = false;
			alreadyCleared = false;


		}

	}


	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {

 		//we don't want to use menu option clicked for venge other, so we use this
		var varbit = event.getVarbitId();
		if( varbit == Varbits.VENGEANCE_COOLDOWN){
			int vengCooldownVarb = event.getValue();

			if( vengCooldownVarb == 1){
				lastVengeanceCast = Instant.now();
				overlayManager.remove(overlay);
				alreadyNotified = false;
				alreadyCleared = false;
			}
		}

	}


	@Provides
	VengeHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VengeHelperConfig.class);
	}
}
