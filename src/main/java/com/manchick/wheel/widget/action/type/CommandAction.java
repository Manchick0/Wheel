package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class CommandAction extends Action {

    public static final MapCodec<CommandAction> CODEC = Codec.STRING.fieldOf("command").xmap(CommandAction::new, CommandAction::getCommand);

    final String command;

    public CommandAction(String command){
        if(command.startsWith("/")){
            this.command = command.substring(1);
        } else this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void run(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        assert player != null;
        player.networkHandler.sendChatCommand(command);
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.COMMAND;
    }
}
