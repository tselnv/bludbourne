package io.robokong.bludbourne;

import com.badlogic.gdx.graphics.g2d.Batch;

public class CastleDoomMap extends Map{
    private static String _mapPath = "maps/castle_of_doom.tmx";

    CastleDoomMap(){
        super(MapFactory.MapType.CASTLE_OF_DOOM, _mapPath);
    }

    @Override
    public void updateMapEntities(Batch batch, float delta){
        for( Entity entity : _mapEntities ){
            entity.update(this, batch, delta);
        }
    }

}
