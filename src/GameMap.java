import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameMap extends Fenetre {

    //PROP
   public   HashMap<Position, Tile> left;
   public   HashMap<Position, Tile> right;
   int      tileSize = 50;
    List<Position> caisses;

    //CONSTR
    public GameMap(String map) {
        super(0, 0, Color.BLACK);

        this.setLayout(new BorderLayout());

        setPreferredSize(new Dimension(1200,900));
        setTitle("Labyrinth");
        setLocationRelativeTo(null);

        setUpLabyrinths(map);
        drawMaps();

        //Create btn for refreshing page
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.black);
        menuBar.setBorderPainted(false);

        JButton btn_refresh = new JButton("RESTART");
        btn_refresh.setBackground(new Color(243,101,71));
        btn_refresh.setForeground(new Color(255,255,255));
        btn_refresh.setBorderPainted(false);
        btn_refresh.setFocusPainted(false);
        btn_refresh.setFocusable(true);
        btn_refresh.requestFocus();
        btn_refresh.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn_refresh.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn_refresh.setPreferredSize(new Dimension(250,30));
        btn_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                new GameEngine().setUpGame(map);
            }
        });

        menuBar.add(btn_refresh);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    //METH
    // récupère la data des labyrinths dans le fichier JSON
    public void setUpLabyrinths(String mapAJouer) {
        JSONParser parser = new JSONParser();
        int posYTile, posXTile; //px

        HashMap<Position, Tile> currentLabyrinth = new HashMap<>();
        caisses = new ArrayList();

        try {
            Object obj = parser.parse(new FileReader("ressources/maps.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject map = (JSONObject) jsonObject.get(mapAJouer);

            JSONArray premierNiveau = (JSONArray) map.get("left"); // pas besoin de prévoir le programme pour gérer n labyrinthes
            JSONArray secondNiveau = (JSONArray) map.get("right"); // donc on récupère à la main le 1er ainsi que le second
            //System.out.println(map);

            Map<JSONArray, Position> maps = new HashMap<>();
            maps.put(premierNiveau, new Position(20, 20)); // Offset for left map
            maps.put(secondNiveau, new Position(600, 20)); // Offset for right map

            for (Map.Entry<JSONArray, Position> currentMap : maps.entrySet()) {

                JSONArray niveau = currentMap.getKey();
                Position pos = currentMap.getValue();
                posYTile = pos.posY;

                for (Object line : niveau) {
                    posYTile += tileSize;
                    posXTile = pos.posX - tileSize;

                    for (Object tile : (ArrayList) line) {
                        posXTile += tileSize;
                        Position position = new Position(posXTile, posYTile);
                        if (tile.toString().equals("BOX")){
                            caisses.add(position);
                            currentLabyrinth.put(position, new Tile(Tile.TypeCase.FLOOR, position));
                        }
                        else currentLabyrinth.put(position, new Tile(Tile.TypeCase.valueOf(tile.toString()), position));
                    }
                }
                if (niveau == premierNiveau) left = (HashMap<Position, Tile>) currentLabyrinth.clone();
                else right = (HashMap<Position, Tile>) currentLabyrinth.clone();
                currentLabyrinth = new HashMap<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // affiche les labyrinthes
    public void drawMaps() {
        for (HashMap<Position, Tile> map : List.of(left, right)) {
            for (var entry : map.entrySet()) {
                entry.getValue().afficher(this);
            }
        }
    }
}
