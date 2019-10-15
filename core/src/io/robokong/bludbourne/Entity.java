package io.robokong.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import io.robokong.bludbourne.utility.Utility;
import sun.util.resources.cldr.ca.CalendarData_ca_ES;

import java.util.ArrayList;
import java.util.UUID;

public class Entity {

    private static final String TAG = Entity.class.getSimpleName();
    private Json _json;
    private EntityConfig _entityConfig;

    public static enum Direction{
        UP,RIGHT,DOWN,LEFT;

        static public Direction getRandomNext(){
            return Direction.values()[MathUtils.random(Direction.values().length -1)];
        }

        public Direction getOpposite(){
            if (this == LEFT){
                return RIGHT;
            } else if (this == RIGHT){
                return LEFT;
            } else if (this == UP){
                return DOWN;
            } else{
                return UP;
            }
        }
    }

    public static enum State{
        IDLE,
        WALKING,
        IMMOBILE; //This should always be last

        static public State getRandomNext(){
            //Ignore IMMOBILE which should be last state
            return State.values()[MathUtils.random(State.values().length -2)];
        }
    }

    public static enum AnimationType{
        WALK_LEFT,
        WALK_RIGHT,
        WALK_UP,
        WALK_DOWN,
        IDLE,
        IMMOBILE
    }

    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;

    private static final int MAX_COMPONENTS = 5;
    private Array<Component> _components;

    private InputComponent _inputComponent;
    private GraphicsComponent _graphicsComponent;
    private PhysicsComponent _physicsComponent;

    public Entity(InputComponent inputComponent, GraphicsComponent graphicsComponent, PhysicsComponent physicsComponent) {
        _entityConfig = new EntityConfig();
        _json = new Json();

        _components = new Array<Component>(MAX_COMPONENTS);

        this._inputComponent = inputComponent;
        this._graphicsComponent = graphicsComponent;
        this._physicsComponent = physicsComponent;

        _components.add(_inputComponent);
        _components.add(_graphicsComponent);
        _components.add(_physicsComponent);
    }

    public EntityConfig getEntityConfig() {
        return _entityConfig;
    }

    public void sendMessage(Component.MESSAGE messageType, String ... args){
        String fullMessage = messageType.toString();

        for(String string: args){
            fullMessage += Component.MESSAGE_TOKEN + string;
        }

        for(Component component: _components){
            component.receiveMessage(fullMessage);
        }
    }

    public void update(MapManager mapMgr, Batch batch, float delta){
        _inputComponent.update(this, delta);
        _physicsComponent.update(this, mapMgr, delta);
        _graphicsComponent.update(this, mapMgr, batch, delta);
    }

    public void dispose(){
        for(Component component: _components){
            component.dispose();
        }
    }

    public Rectangle getCurrentBoundingBox(){
        return _physicsComponent._boundingBox;
    }

    static public EntityConfig getEntityConfig(String configFilePath){
        Json json = new Json();
        return json.fromJson(EntityConfig.class, Gdx.files.internal(configFilePath));
    }

    public void setEntityConfig(EntityConfig entityConfig){
        this._entityConfig = entityConfig;
    }

    static public Array<EntityConfig> getEntityConfigs(String configFilePath){
        Json json = new Json();
        Array<EntityConfig> configs = new Array<EntityConfig>();

        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));

        for( JsonValue jsonVal: list){
            configs.add(json.readValue(EntityConfig.class, jsonVal));
        }
        return configs;
    }

    //    private static final String _defaultSpritePath = "sprites/characters/Warrior.png";
//
//    private Vector2 _velocity;
//    private String _entityID;
//
//    private Direction _currentDirection = Direction.LEFT;
//    private Direction _previousDirection = Direction.UP;
//
//    private Animation _walkLeftAnimation;
//    private Animation _walkRightAnimation;
//    private Animation _walkUpAnimation;
//    private Animation _walkDownAnimation;
//
//    private Array<TextureRegion> _walkLeftFrames;
//    private Array<TextureRegion> _walkRightFrames;
//    private Array<TextureRegion> _walkUpFrames;
//    private Array<TextureRegion> _walkDownFrames;
//
//    private Vector2 _nextPlayerPosition;
//    private Vector2 _currentPlayerPosition;
//    protected State _state = State.IDLE;
//    protected float _frameTime = 0f;
//    protected Sprite _frameSprite = null;
//    protected TextureRegion _currentFrame = null;
//


//

//
//
//
//    public void initEntity(){
//        this._entityID = UUID.randomUUID().toString();
//        this._nextPlayerPosition = new Vector2();
//        this._currentPlayerPosition = new Vector2();
//        this.boundingBox = new Rectangle();
//        this._velocity = new Vector2(2f,2f);
//
//        Utility.loadTextureAsset(_defaultSpritePath);
//        loadDefaultSprite();
//        loadAllAnimation();
//    }
//
//    private void loadAllAnimation() {
//        //Walking animation
//        Texture texture = Utility.getTextureAsset(_defaultSpritePath);
//        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_WIDTH);
//        _walkDownFrames = new Array<TextureRegion>(4);
//        _walkUpFrames = new Array<TextureRegion>(4);
//        _walkLeftFrames = new Array<TextureRegion>(4);
//        _walkRightFrames = new Array<TextureRegion>(4);
//
//        for (int i =0; i<4; i++){
//            for (int j = 0; j<4; j++){
//                TextureRegion region = textureFrames[i][j];
//                if(region == null){
//                    Gdx.app.debug(TAG, "Got null animation frame " + i + ", " + j);
//                }
//                switch (i){
//                    case 0:
//                        _walkDownFrames.insert(j,region);
//                        break;
//                    case 1:
//                        _walkLeftFrames.insert(j,region);
//                        break;
//                    case 2:
//                        _walkRightFrames.insert(j,region);
//                        break;
//                    case 3:
//                        _walkUpFrames.insert(j,region);
//                        break;
//                }
//            }
//        }
//
//        _walkDownAnimation = new Animation(0.25f, _walkDownFrames, Animation.PlayMode.LOOP);
//        _walkLeftAnimation = new Animation(0.25f, _walkLeftFrames, Animation.PlayMode.LOOP);
//        _walkRightAnimation = new Animation(0.25f, _walkRightFrames, Animation.PlayMode.LOOP);
//        _walkUpAnimation = new Animation(0.25f, _walkUpFrames, Animation.PlayMode.LOOP);
//    }
//
//    private void loadDefaultSprite() {
//        Texture texture = Utility.getTextureAsset(_defaultSpritePath);
//        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
//        _frameSprite = new Sprite(textureFrames[0][0].getTexture(),0,0,FRAME_WIDTH, FRAME_HEIGHT);
//        _currentFrame = textureFrames[0][0];
//    }
//
//    public void init(float startX, float startY) {
//        this._currentPlayerPosition.x = startX;
//        this._currentPlayerPosition.y = startY;
//
//        this._nextPlayerPosition.x = startX;
//        this._nextPlayerPosition.y = startY;
//    }
//
//    public void setBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced) {
//        //Update the current bounding box
//        float width;
//        float height;
//
//        float widthReuctionAmount = 1.0f - percentageWidthReduced; //.8f for 20% (1 - .20)
//        float heightReuctionAmount = 1.0f - percentageHeightReduced; //.8f for 20% (1 - .20)
//
//        if(widthReuctionAmount > 0 && widthReuctionAmount < 1 ){
//            width = FRAME_WIDTH * widthReuctionAmount;
//        } else {
//            width = FRAME_WIDTH;
//        }
//
//        if(heightReuctionAmount > 0 && heightReuctionAmount < 1 ){
//            height = FRAME_HEIGHT * heightReuctionAmount;
//        } else {
//            height = FRAME_HEIGHT;
//        }
//
//        if(width == 0 || height == 0){
//            Gdx.app.debug(TAG, "Width and Height are 0!! " + width + ":" + height);
//        }
//
//        //Need to account for the unitscale, since the map coordinates will be in pixels
//        float minX;
//        float minY;
//        if( MapManager.UNIT_SCALE > 0 ){
//            minX = _nextPlayerPosition.x / MapManager.UNIT_SCALE;
//            minY = _nextPlayerPosition.y / MapManager.UNIT_SCALE;
//        } else {
//            minX = _nextPlayerPosition.x;
//            minY = _nextPlayerPosition.y;
//        }
//
//        boundingBox.set(minX, minY, width, height);
//    }
//
//    public Sprite getFrameSprite() {
//        return _frameSprite;
//    }
//
//    public void update(float delta) {
//        _frameTime = (_frameTime + delta) % 5;//Want to avoid overflow
//        //We want the hitbox to be at the feet for a better feel
//
//        setBoundingBoxSize(0f, 0.5f);
//    }
//
//    public TextureRegion getFrame() {
//        return _currentFrame;
//    }
//
//    public void dispose(){
//        Utility.unloadAsset(_defaultSpritePath);
//    }
//
//    public void setState(State _state) {
//        this._state = _state;
//    }
//
//    public Vector2 getCurrentPosition() {
//        return _currentPlayerPosition;
//    }
//
//    public void setCurrentPosition(float currentPositionX, float currentPositionY) {
//        _frameSprite.setX(currentPositionX);
//        _frameSprite.setY(currentPositionY);
//        this._currentPlayerPosition.x = currentPositionX;
//        this._currentPlayerPosition.y = currentPositionY;
//    }
//
//    public void setDirection(Direction direction, float deltaTime) {
//        this._previousDirection = this._currentDirection;
//        this._currentDirection = direction;
//
//        //Look into the appropriate variable when changing position
//
//        switch (_currentDirection){
//            case DOWN:
//                _currentFrame = (TextureRegion) _walkDownAnimation.getKeyFrame(_frameTime);
//                break;
//            case UP:
//                _currentFrame = (TextureRegion) _walkUpAnimation.getKeyFrame(_frameTime);
//                break;
//            case LEFT:
//                _currentFrame = (TextureRegion) _walkLeftAnimation.getKeyFrame(_frameTime);
//                break;
//            case RIGHT:
//                _currentFrame = (TextureRegion) _walkRightAnimation.getKeyFrame(_frameTime);
//                break;
//            default:
//                break;
//        }
//    }
//
//    public void setNextPositionToCurrent(){
//        setCurrentPosition(_nextPlayerPosition.x, _nextPlayerPosition.y);
//    }
//
//    public void calculateNextPosition(Direction currentDirection, float deltaTime){
//        float textX = _currentPlayerPosition.x;
//        float textY = _currentPlayerPosition.y;
//
//        _velocity.scl(deltaTime);
//
//        switch (currentDirection){
//            case LEFT:
//                textX -= _velocity.x;
//                break;
//            case RIGHT:
//                textX += _velocity.x;
//                break;
//            case UP:
//                textY += _velocity.y;
//                break;
//            case DOWN:
//                textY -= _velocity.y;
//                break;
//            default:
//                break;
//        }
//
//        _nextPlayerPosition.x = textX;
//        _nextPlayerPosition.y = textY;
//
//        //velocity
//        _velocity.scl(1 / deltaTime);
//    }
}
