package io.robokong.bludbourne;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class PhysicsComponent implements Component {

    public abstract void update(Entity entity, MapManager mapMgr, float delta);

    public static Rectangle boundingBox;
    public Rectangle _boundingBox;
    protected BoundingBoxLocation _boundingBoxLocation;

    public static enum BoundingBoxLocation{
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        CENTER,
    }

    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapMgr){
        Array<Entity> entities = mapMgr.getCurrentMapEntities();
        boolean isCollisionWithMapEntities = false;

        for (Entity mapEntity: entities){
            //Check for testing against self
            if(mapEntity.equals(entity)){
                continue;
            }

            Rectangle targetRect = mapEntity.getCurrentBoundingBox();

            if(_boundingBox.overlaps(targetRect)){
                //Collision
                entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
                isCollisionWithMapEntities = true;
                break;
            }
        }
        return isCollisionWithMapEntities;
    }

    protected boolean isCollision(Entity entitySource, Entity entityTarget){
        boolean isCollisionWithMapEntities = false;

        if( entitySource.equals(entityTarget)){
            return false;
        }

        //if(entitySource.getCurrentBoundingBox().overlaps())
    }
}
