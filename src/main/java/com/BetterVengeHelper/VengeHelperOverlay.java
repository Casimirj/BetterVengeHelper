package com.portaguy;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import com.BetterVengeHelper.VengeHelperConfig;
import net.runelite.api.Client;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class VengeHelperOverlay extends OverlayPanel
{

    private final VengeHelperConfig config;
    private final Client client;

    @Inject
    private VengeHelperOverlay(VengeHelperConfig config, Client client)
    {
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add((LineComponent.builder())
                .left("You need to cast Vengeance!")
                .build());

        if (config.shouldFlash()) {
            if (client.getGameCycle() % 40 >= 20)
            {
                panelComponent.setBackgroundColor(config.flashColor1());
            } else
            {
                panelComponent.setBackgroundColor(config.flashColor2());
            }
        } else {
            panelComponent.setBackgroundColor(config.flashColor1());
        }

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        return panelComponent.render(graphics);
    }
}