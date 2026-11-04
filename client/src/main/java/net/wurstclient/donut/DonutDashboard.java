/*
 * Donut Client dashboard, 2026.
 * GPL-3.0-or-later. Uses the Wurst Client module API.
 * Original Wurst Client copyright Wurst-Imperium and contributors.
 */
package net.wurstclient.donut;

import java.util.*;
import java.nio.file.*;
import java.io.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.wurstclient.WurstClient;
import net.wurstclient.hack.Hack;
import net.wurstclient.navigator.NavigatorFeatureScreen;
import net.wurstclient.navigator.NavigatorMainScreen;
import net.wurstclient.clickgui.screens.ClickGuiScreen;

/** Searchable dashboard backed by real Wurst modules and their persisted settings. */
public final class DonutDashboard extends Screen {
    private static final String[] TABS = {"All modules", "Favorites", "Enabled", "BaseFinding", "Combat", "Render", "Movement", "Blocks", "Items", "Other", "Chat", "Fun"};
    private final Set<String> favorites = new TreeSet<>();
    private String selected = "All modules", query = "", notice = "";
    private int page, left, top, panelWidth, panelHeight, rows;
    private int tabStep() { return Math.min(23, Math.max(10,(panelHeight-110)/TABS.length)); }
    private EditBox search;
    private List<Hack> visible = List.of();
    private final Path favoritesFile = WurstClient.INSTANCE.getWurstFolder().resolve("donut-favorites.txt");
    public DonutDashboard() {
        super(Component.literal("Donut Client"));
        try { if(Files.exists(favoritesFile)) favorites.addAll(Files.readAllLines(favoritesFile)); }
        catch(IOException e) { notice = "Could not load favorites"; }
    }
    @Override protected void init() {
        panelWidth = Math.min(700, width - 16); panelHeight = Math.min(440, height - 16);
        left = (width-panelWidth)/2; top = (height-panelHeight)/2;
        rows = Math.max(1, (panelHeight - 112)/43);
        search = new EditBox(font, left+126, top+40, Math.max(60,panelWidth-142), 20, Component.literal("Search modules"));
        search.setBordered(false); search.setTextColor(0xFFEEE8FF);
        search.setMaxLength(80); search.setHint(Component.literal("Search modules..."));
        search.setValue(query); search.setResponder(value -> {query=value;page=0;});
        addRenderableWidget(search);
    }
    private List<Hack> filtered() {
        return WurstClient.INSTANCE.getHax().getAllHax().stream()
            .filter(h -> h.getCategory()!=null)
            .filter(h -> selected.equals("All modules") || selected.equals("Favorites") && favorites.contains(h.getName())
                || selected.equals("Enabled") && h.isEnabled()
                || selected.equals("BaseFinding") && Set.of("BaseFinder", "ChestESP", "BlockESP", "Search", "CaveFinder", "MobSpawnerESP", "Freecam", "Fullbright", "X-Ray").contains(h.getName())
                || h.getCategory().getName().equals(selected))
            .filter(h -> (h.getName()+" "+h.getDescription()).toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
            .sorted(Comparator.comparing(Hack::getName)).toList();
    }
    @Override public void renderBackground(GuiGraphics g,int mx,int my,float delta) {}
    @Override public boolean isPauseScreen() {return false;}
    private boolean inside(double mx,double my,int x,int y,int w,int h) {return mx>=x&&mx<x+w&&my>=y&&my<y+h;}
    private void text(GuiGraphics g,String s,int x,int y,int color) {g.drawString(font,s,x,y,color,false);}
    @Override public void render(GuiGraphics g,int mx,int my,float delta) {
        g.fill(0,0,width,height,0x99060910);
        g.fill(left-1,top-1,left+panelWidth+1,top+panelHeight+1,0xFF383449);
        g.fill(left,top,left+panelWidth,top+panelHeight,0xFA11121C);
        g.fill(left,top,left+112,top+panelHeight,0xFF171823);
        g.fill(left,top,left+panelWidth,top+2,0xFFA590FF);
        text(g,"DONUT",left+14,top+14,0xFFF3EFFC);
        text(g,"CLIENT / 02",left+14,top+28,0xFF9F91CC);
        text(g,"MODULE LIBRARY",left+126,top+16,0xFFE9E6F5);
        g.fill(left+122,top+35,left+panelWidth-12,top+64,0xFF242331);
        int y=top+54;
        for(String tab:TABS) {
            boolean active=selected.equals(tab);
            if(active||inside(mx,my,left+6,y-5,100,tabStep()))g.fill(left+6,y-5,left+106,y+tabStep()-5,active?0xFF352B53:0xFF232433);
            if(active)g.fill(left+6,y-5,left+8,y+tabStep()-5,0xFFAE94FF);
            text(g,tab,left+15,y,active?0xFFE8DCFF:0xFF9597AD); y+=tabStep();
            if(y>top+panelHeight-48)break;
        }
        List<Hack> all=filtered();int pageCount=Math.max(1,(all.size()+rows-1)/rows);page=Math.max(0,Math.min(page,pageCount-1));
        visible=all.subList(Math.min(page*rows,all.size()),Math.min((page+1)*rows,all.size()));
        int x=left+126,w=panelWidth-142;y=top+72;
        for(Hack h:visible) {
            boolean hover=inside(mx,my,x,y,w,38);
            g.fill(x,y,x+w,y+38,hover?0xFF292639:0xFF1C1D2B);
            g.fill(x,y,x+2,y+38,h.isEnabled()?0xFFAB91FF:0xFF373648);
            text(g,h.getName(),x+10,y+7,h.isEnabled()?0xFFD5C2FF:0xFFE6E6F0);
            String description=h.getDescription().replaceAll("§.","").replace('\n',' ');
            text(g,font.plainSubstrByWidth(description,Math.max(10,w-88)),x+10,y+23,0xFF9395AA);
            text(g,favorites.contains(h.getName())?"*":"+",x+w-58,y+8,0xFFD8BDFF);
            g.fill(x+w-38,y+9,x+w-10,y+22,h.isEnabled()?0xFF8B6ADD:0xFF414052);
            g.fill(h.isEnabled()?x+w-21:x+w-36,y+11,h.isEnabled()?x+w-12:x+w-27,y+20,0xFFF5EEFF);
            y+=43;
        }
        if(all.isEmpty()) {text(g,"No matching modules",x,top+88,0xFFE6E6F0);text(g,"Try a different search or category.",x,top+104,0xFF9395AA);}
        int bottom=top+panelHeight;
        text(g,all.size()+" modules  |  "+(page+1)+" / "+pageCount,x,bottom-32,0xFF9D97B4);
        text(g,"< PREV",left+panelWidth-115,bottom-32,0xFFD6C5FF);
        text(g,"NEXT >",left+panelWidth-58,bottom-32,0xFFD6C5FF);
        text(g,font.plainSubstrByWidth(notice.isEmpty()?"LMB: toggle | RMB: settings | Wheel: pages":notice,w),x,bottom-17,0xFF9793AC);
        text(g,"Wurst-based",left+12,bottom-34,0xFF88849D);
        text(g,"Classic GUI",left+12,bottom-18,0xFFBEA6FF);
        g.guiRenderState.up();
        super.render(g,mx,my,delta);
    }
    @Override public boolean mouseClicked(MouseButtonEvent e,boolean doubleClick) {
        double mx=e.x(),my=e.y();int y=top+54;
        for(String tab:TABS) {
            if(inside(mx,my,left+6,y-5,100,tabStep())) {selected=tab;page=0;return true;}
            y+=tabStep();if(y>top+panelHeight-48)break;
        }
        if(inside(mx,my,left+10,top+panelHeight-24,95,20)) {minecraft.setScreen(new ClickGuiScreen(WurstClient.INSTANCE.getGui()));return true;}
        if(inside(mx,my,left+panelWidth-120,top+panelHeight-38,55,18)) {page=Math.max(0,page-1);return true;}
        if(inside(mx,my,left+panelWidth-60,top+panelHeight-38,55,18)) {page++;return true;}
        int x=left+126,w=panelWidth-142;y=top+72;
        for(Hack h:visible) {
            if(inside(mx,my,x,y,w,38)) {
                if(e.button()==1) minecraft.setScreen(new NavigatorFeatureScreen(h,this));
                else if(e.button()==0 && inside(mx,my,x+w-65,y,24,38)) {
                    if(!favorites.remove(h.getName()))favorites.add(h.getName());
                    try { Files.write(favoritesFile,favorites);notice=""; } catch(IOException ex) {notice="Could not save favorites";}
                } else if(e.button()==0)h.setEnabled(!h.isEnabled());
                return true;
            }
            y+=43;
        }
        return super.mouseClicked(e,doubleClick);
    }
    @Override public boolean mouseScrolled(double mx,double my,double horizontal,double vertical) {
        if(vertical!=0)page=Math.max(0,page+(vertical<0?1:-1));return true;
    }
}
