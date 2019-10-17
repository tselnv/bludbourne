package io.robokong.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.Hashtable;

public class MapFactory {
    private static final String TAG = MapFactory.class.getSimpleName();

    //All maps for the game
    private static Hashtable<MapType,Map> _mapTable = new Hashtable<MapType, Map>();

    public static enum MapType{
        TOP_WORLD,
        TOWN,
        CASTLE_OF_DOOM
    }

    private static Map currentMap = null;
    private static OrthographicCamera _camera;

    static public Map getMap(MapType mapType){
        Map map = null;
        switch(mapType){
            case TOP_WORLD:
                map = _mapTable.get(MapType.TOP_WORLD);
                if( map == null ){
                    map = new TopWorldMap();
                    _mapTable.put(MapType.TOP_WORLD, map);
                }
                break;
            case TOWN:
                map = _mapTable.get(MapType.TOWN);
                if( map == null ){
                    map = new TownMap();
                    _mapTable.put(MapType.TOWN, map);
                }
                break;
            case CASTLE_OF_DOOM:
                map = _mapTable.get(MapType.CASTLE_OF_DOOM);
                if( map == null ){
                    map = new CastleDoomMap();
                    _mapTable.put(MapType.CASTLE_OF_DOOM, map);
                }
                break;
            default:
                break;
        }
        if( map == null ){
            Gdx.app.debug(TAG, "Map does not exist!  ");
            return null;
        }
        currentMap = map;
        Gdx.app.debug(TAG, "Player Start: (" + currentMap.getPlayerStart().x + "," + currentMap.getPlayerStart().y + ")");
        map.setMapChanged(true);
        return map;
    }

    public static Map getCurrentMap() {
        return currentMap;
    }

    public static OrthographicCamera getCamera() {
        return _camera;
    }

    public static void setCamera(OrthographicCamera _camera) {
        MapFactory._camera = _camera;
    }
}
