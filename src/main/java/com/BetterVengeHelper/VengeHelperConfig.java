package com.BetterVengeHelper;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("example")
public interface VengeHelperConfig extends Config
{
	@ConfigItem(
			keyName = "vengeReminderTimeoutSeconds",
			name = "Timeout Venge Box",
			description = "The duration in seconds before the Venge box disappears.",
			position = 2
	)
	@Units(Units.SECONDS)
	default int vengeReminderTimeoutSeconds()
	{
		return 120;
	}


	@ConfigItem(
			keyName = "hideReminderHotkey",
			name = "Hide Reminder Hotkey",
			description = "Use this hotkey to hide the reminder box.",
			position = 5
	)
	default Keybind hideReminderHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "shouldFlash",
			name = "Flash the Reminder Box",
			description = "Makes the reminder box flash between the defined colors.",
			position = 3
	)
	default boolean shouldFlash() { return false; }


	@Alpha
	@ConfigItem(
			keyName = "flashColor1",
			name = "Flash Color #1",
			description = "The first color to flash between, also controls the non-flashing color.",
			position = 6
	)
	default Color flashColor1() { return new Color(0, 255, 0, 150); }

	@Alpha
	@ConfigItem(
			keyName = "flashColor2",
			name = "Flash Color #2",
			description = "The second color to flash between.",
			position = 7
	)
	default Color flashColor2() { return new Color(70, 61, 50, 150); }




}
