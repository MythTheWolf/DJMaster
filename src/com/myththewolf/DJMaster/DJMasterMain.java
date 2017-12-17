package com.myththewolf.DJMaster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.myththewolf.BotServ.lib.API.invoke.BotPlugin;
import com.myththewolf.BotServ.lib.API.invoke.ImplBotPlugin;
import com.myththewolf.BotServ.lib.API.invoke.PluginAdapater;
import com.myththewolf.BotServ.lib.tool.Utils;
import com.myththewolf.DJMaster.lib.bots.Maps;
import com.myththewolf.DJMaster.lib.bots.StreamPlayer;
import com.myththewolf.DJMaster.lib.commands.IsAdmin;
import com.myththewolf.DJMaster.lib.commands.IsDj;
import com.myththewolf.DJMaster.lib.commands.PlayHere;
import com.myththewolf.DJMaster.lib.commands.PlayOn;
import com.myththewolf.DJMaster.lib.commands.PlayStream;
import com.myththewolf.DJMaster.lib.commands.mkmount;
import com.myththewolf.DJMaster.lib.commands.shardify;

public class DJMasterMain implements PluginAdapater {

  public void onDisable() {
    // TODO Auto-generated method stub

  }

  public void onEnable() {
    BotPlugin arg0 = getPlugin();
    File config = arg0.getConfig();
    ImplBotPlugin cast = (ImplBotPlugin) arg0;
    InputStream internal = cast.getInternalResource(cast.getSelfJar(), "config.json");
    if (!config.exists()) {
      try {
        java.nio.file.Files.copy(internal, config.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      IOUtils.closeQuietly(internal);
    }

    try {
      String token = Utils.readFile(config);
      Maps.config = new JSONObject(token);
      JSONArray BOTS = Maps.config.getJSONArray("shards");
      BOTS.forEach(iteration -> {
        System.out.println(iteration);
        Maps.PLAYERS.add(new StreamPlayer(iteration.toString()));
      });
      arg0.registerCommand("!shardify", new shardify());
      arg0.registerCommand("!play", new PlayStream());
      arg0.registerCommand("!isdj", new IsDj());
      arg0.registerCommand("!addchannel", new PlayHere());
      arg0.registerCommand("!playon", new PlayOn());
      arg0.registerCommand("!mkmount", new mkmount());
      arg0.registerCommand("!stop", exec -> {
        String ID = exec.getSender().getId();
        List<StreamPlayer> PL =
            Maps.PLAYERS.stream().filter(it -> it.getDJ() != null && it.getDJ().getId().equals(ID))
                .collect(Collectors.toList());
        if (!(PL.size() > 0)) {
          exec.failed("You are not currently streaming!");
        } else {
          PL.forEach(con -> con.shutdown());
        }
      });
      arg0.registerCommand("!isadmin", new IsAdmin());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }

  @Override
  public void reload() {
    // TODO Auto-generated method stub

  }

}
