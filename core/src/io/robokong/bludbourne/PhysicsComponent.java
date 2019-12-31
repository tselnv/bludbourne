package io.robokong.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public abstract class PhysicsComponent implements Component {
    private static final String TAG = PhysicsComponent.class.getSimpleName();

    public abstract void update(Entity entity, MapManager mapMgr, float delta);

    public Rectangle boundingBox;
    protected Entity.Direction currentDirection;
    protected Vector2 nextEntityPosition;
    protected Vector2 currentEntityPosition;
    protected Vector2 velocity;

    protected Json json;

    protected BoundingBoxLocation boundingBoxLocation;

    public static enum BoundingBoxLocation{
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        CENTER,
    }

    public PhysicsComponent() {
        nextEntityPosition = new Vector2(0,0);
        currentEntityPosition = new Vector2(0,0);
        velocity = new Vector2(2f,2f);
        boundingBox = new Rectangle();
        json = new Json();
        boundingBoxLocation = BoundingBoxLocation.BOTTOM_LEFT;
    }

    @Override
    public void receiveMessage(String fullMessage) {
        //TODO
    }

    @Override
    public void dispose() {
    }

    protected boolean isCollisionWithMapEntities(Entity entity, MapManager mapMgr){
        Array<Entity> entities = mapMgr.getCurrentMapEntities();
        boolean isCollisionWIthMapEntities = false;

        for (Entity mapEntity: entities){
            // Check for testing against itself
            if( mapEntity.equals(entity)) continue;

            Rectangle targetRect = mapEntity.getCurrentBoundingBox();

            if( this.boundingBox.overlaps(targetRect)){
                //Collision
                entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
                isCollisionWIthMapEntities = true;
                break;
            }

        }
        return isCollisionWIthMapEntities;
    }


    protected boolean isCollision(Entity entitySource, Entity entityTarget){

        boolean isCollisionWIthMapEntities = false;

        // Check for testing against itself
        if( entitySource.equals(entityTarget)) return false;


        if( entitySource.getCurrentBoundingBox().overlaps(entityTarget.getCurrentBoundingBox())){
            //Collision
            entitySource.sendMessage(MESSAGE.COLLISION_WITH_ENTITY);
            isCollisionWIthMapEntities = true;
        }
        return isCollisionWIthMapEntities;
    }

    protected boolean isCollisionsWithMapLayer(Entity entity, MapManager mapMgr){
        MapLayer mapCollisionLayer = mapMgr.getCollisionLayer();

        if(mapCollisionLayer == null) return false;

        Rectangle rectangle = null;
        for(MapObject object: mapCollisionLayer.getObjects()){
            if( object instanceof RectangleMapObject){
                rectangle = ((RectangleMapObject) object).getRectangle();
                if (boundingBox.overlaps(rectangle)){
                    //collision
                    entity.sendMessage(MESSAGE.COLLISION_WITH_MAP);
                    return true;
                }
            }
        }
        return false;
    }

    public void setNextPositionToCurrent(Entity entity) {
        this.currentEntityPosition.x = nextEntityPosition.x;
        this.currentEntityPosition.y = nextEntityPosition.y;
        //Gdx.app.debug(TAG, "SETTING Current Position " + entity.getEntityConfig().getEntityID() + ": (" + _currentEntityPosition.x + "," + _currentEntityPosition.y + ")");
        entity.sendMessage(MESSAGE.CURRENT_POSITION, json.toJson(currentEntityPosition));
    }

    public void calculateNextPosition(float deltaTime){
        if(currentDirection == null) return;

        float testX = currentEntityPosition.x;
        float testY = currentEntityPosition.y;

        velocity.scl(deltaTime);

        switch (currentDirection){
            case LEFT:
                testX -= velocity.x;
                break;
            case RIGHT:
                testX += velocity.x;
                break;
            case UP:
                testY += velocity.y;
                break;
            case DOWN:
                testY -= velocity.y;
                break;
        }

        nextEntityPosition.x = testX;
        nextEntityPosition.y = testY;

        //velocity
        velocity.scl(1/deltaTime);
    }

    protected void initBoundingBox(float percentageWidthReduced, float percentageHeightReduced){
        //update the current bounding box
        float width;
        float height;

        float origWidth = Entity.FRAME_WIDTH;
        float origHeight = Entity.FRAME_HEIGHT;

        float widthReductionAmount = 1.0f - percentageWidthReduced; //0.8f for 20% (1 - 0.2f)
        float heightReductionAmount = 1.0f - percentageHeightReduced; // the same

        if( widthReductionAmount > 0 && widthReductionAmount < 1){
            width = Entity.FRAME_WIDTH * widthReductionAmount;
        } else {
            width = Entity.FRAME_WIDTH;
        }

        if( heightReductionAmount > 0 && heightReductionAmount < 1){
            height = Entity.FRAME_WIDTH * heightReductionAmount;
        } else {
            height = Entity.FRAME_WIDTH;
        }

        if( width ==0 || height == 0 ){
            Gdx.app.debug(TAG, "Width and height are 0!! " + width + ":" + height);
        }

        //Need to account for the unitscale, since the map coordinates will be in pixels
        float minX;
        float minY;
        if ( MapManager.UNIT_SCALE > 0 ){
            minX = nextEntityPosition.x/ MapManager.UNIT_SCALE;
            minY = nextEntityPosition.y/ MapManager.UNIT_SCALE;
        } else {
            minX = nextEntityPosition.x;
            minY = nextEntityPosition.y;
        }
        boundingBox.setWidth(width);
        boundingBox.setHeight(height);

        switch (boundingBoxLocation){
            case BOTTOM_LEFT:
                boundingBox.set(minX, minY, width, height);
                break;
            case BOTTOM_CENTER:
                boundingBox.setCenter(minX + origWidth/2, minY + origHeight/4);
                break;
            case CENTER:
                boundingBox.setCenter(minX + origWidth/2, minY + origHeight/2);
                break;
        }
    }

    protected void updateBoundingBoxPosition(){
        //TODO
    }

}
