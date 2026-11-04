// SPDX-License-Identifier: GPL-3.0-or-later
package dev.donutlab;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.wurstclient.WurstClient;
import net.wurstclient.donut.DonutDashboard;
import net.minecraft.client.gui.components.EditBox;
public class DashboardSmokeTest implements FabricClientGameTest {
 public void runTest(ClientGameTestContext context) {
  context.waitTicks(30);
  try(var world=context.worldBuilder().create()) {
   context.waitTicks(30);
   context.runOnClient(mc -> {
    if(WurstClient.INSTANCE.getHax().countHax()<100)throw new AssertionError("Missing module registry");
    mc.setScreen(null);
    System.out.println("DONUT_MODULE_COUNT="+WurstClient.INSTANCE.getHax().countHax());
   });
   context.getInput().pressKey(org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT);
   context.waitTicks(5);
   context.takeScreenshot("donut-dashboard");
   context.runOnClient(mc -> {
    if(!(mc.screen instanceof DonutDashboard))throw new AssertionError("Dashboard not open");
    for(var child:mc.screen.children())if(child instanceof EditBox edit)edit.setValue("Fullbright");
   });
   context.waitTicks(5);
   context.takeScreenshot("donut-search");
   context.runOnClient(mc -> {
    var h=WurstClient.INSTANCE.getHax().getHackByName("Fullbright");
    h.setEnabled(false);
    int x=(mc.screen.width-Math.min(700,mc.screen.width-16))/2+140;
    int y=(mc.screen.height-Math.min(440,mc.screen.height-16))/2+82;
    mc.screen.mouseClicked(new net.minecraft.client.input.MouseButtonEvent(x,y,new net.minecraft.client.input.MouseButtonInfo(0,0)),false);
    if(!h.isEnabled())throw new AssertionError("Search result toggle failed");
    h.setEnabled(false);
    mc.screen.mouseClicked(new net.minecraft.client.input.MouseButtonEvent(x,y,new net.minecraft.client.input.MouseButtonInfo(1,0)),false);
    if(!(mc.screen instanceof net.wurstclient.navigator.NavigatorFeatureScreen))throw new AssertionError("Settings did not open");
   });
   context.waitTicks(5);
   context.takeScreenshot("donut-settings");
   context.runOnClient(mc -> mc.setScreen(null));
   for(String name:new String[]{"Fullbright","ChestESP","PlayerESP","BaseFinder","Freecam"}) {
    context.runOnClient(mc -> {
     var h=WurstClient.INSTANCE.getHax().getHackByName(name);
     if(h==null)throw new AssertionError("Missing "+name);
     h.setEnabled(true);
     if(!h.isEnabled())throw new AssertionError("Could not enable "+name);
    });
    context.waitTicks(5);
    context.runOnClient(mc -> WurstClient.INSTANCE.getHax().getHackByName(name).setEnabled(false));
   }
   context.waitTicks(5);
  }
 }
}
