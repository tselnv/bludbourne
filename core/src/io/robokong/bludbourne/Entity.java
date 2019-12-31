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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.UUID;

public class Entity {
    private static final String TAG = Entity.class.getSimpleName();

    private Json json;
    private EntityConfig entityConfig;

    public enum State{
        IDLE,
        WALKING,
        IMMOBILE; //This should always be last

        static public State getRandomNext(){
            return State.values()[MathUtils.random(State.values().length - 2)]; //Ignore IMMOBILE which should be last state
        }
    }

    public static enum Direction {
        UP,RIGHT, DOWN, LEFT;
        static public Direction getRandomNext(){
            return Direction.values()[MathUtils.random(Direction.values().length-1)];
        }
        public Direction getOpposite(){
            if ( this == LEFT){
                return RIGHT;
            } else if( this == RIGHT){
                return LEFT;
            } else if( this == UP){
                return DOWN;
            } else return UP;
        }
    }

    enum  AnimationType{
        WALK_LEFT,
        WALK_RIGHT,
        WALK_UP,
        WALK_DOWN,
        IDLE,
        IMMOBILE;
    }

    private String defaultSpritePath = "sprites/Characters/Warrior.png";
    private Vector2 velocity;
    private String entityID;

    private Direction currentDirection = Direction.LEFT;
    private Direction previousDirection = Direction.UP;

    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Array<TextureRegion> walkLeftFrames;
    private Array<TextureRegion> walkRightFrames;
    private Array<TextureRegion> walkUpFrames;
    private Array<TextureRegion> walkDownFrames;

    protected Vector2 nextPlayerPosition;
    protected Vector2 currentPlayerPosition;
    protected State state = State.IDLE;
    protected float frameTime = 0f;
    protected Sprite frameSprite = null;
    protected TextureRegion currentFrame = null;

    public static final int FRAME_WIDTH = 16;
    public static final int FRAME_HEIGHT = 16;

    private static final int MAX_COMPONENTS = 5;
    private Array<Component> components;

    public Rectangle boundingBox;

    private InputComponent inputComponent;
    private PhysicsComponent physicsComponent;
    private GraphicsComponent graphicsComponent;


    public Entity() {
        initEntity();
    }

    public Entity(InputComponent inputComponent, PhysicsComponent physicsComponent, GraphicsComponent graphicsComponent) {
        components = new Array<>(MAX_COMPONENTS);

        this.inputComponent = inputComponent;
        this.physicsComponent = physicsComponent;
        this.graphicsComponent = graphicsComponent;

        components.add(inputComponent);
        components.add(physicsComponent);
        components.add(graphicsComponent);
    }

    public void initEntity(){
        this.entityID = UUID.randomUUID().toString();
        this.nextPlayerPosition = new Vector2();
        this.currentPlayerPosition = new Vector2();
        this.boundingBox = new Rectangle();
        this.velocity = new Vector2(2f,2f);

        Utility.loadTextureAsset( defaultSpritePath);
        loadDefaultSprite();
        loadAllAnimations();
    }

    public void init(float startX, float startY) {
        this.currentPlayerPosition.x = startX;
        this.currentPlayerPosition.y = startY;

        this.nextPlayerPosition.x = startX;
        this.nextPlayerPosition.y = startY;
    }

    public void update(float delta) {
        frameTime = (frameTime + delta) % 5; // Want to avoid overflow

        //We want the hitbox to be at the feet for a better feel
        setBoundingBoxSize(0f, 0.5f);
    }

    public void update(MapManager mapMgr, Batch batch, float delta) {
        inputComponent.update(this, delta);
        physicsComponent.update(this, mapMgr, delta);
        graphicsComponent.update(this, mapMgr, batch, delta);
    }

    public TextureRegion getFrame() {
        return currentFrame;
    }

    public void setNextPositionToCurrent() {
        setCurrentPosition(nextPlayerPosition.x, nextPlayerPosition.y);
    }

    public void dispose() {
        for( Component component: components){
            component.dispose();
        }
    }

    public Vector2 getCurrentPosition() {
        return currentPlayerPosition;
    }

    public Rectangle getCurrentBoundingBox(){
        return physicsComponent.boundingBox;
    }

    public void setCurrentPosition(float currentPositionX, float currentPositionY) {
        frameSprite.setX(currentPositionX);
        frameSprite.setY(currentPositionY);
        this.currentPlayerPosition.x = currentPositionX;
        this.currentPlayerPosition.y = currentPositionY;
    }

    public void calculateNextPosition(Direction currentDirection, float deltaTime){
        float testX = currentPlayerPosition.x;
        float testY = currentPlayerPosition.y;

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

        nextPlayerPosition.x = testX;
        nextPlayerPosition.y = testY;

        //velocity
        velocity.scl(1/deltaTime);
    }

    public void setDirection(Direction direction, float deltaTime){
        this.previousDirection = this.currentDirection;
        this.currentDirection = direction;

        //Look into the appropriate variable when changing position
        switch (currentDirection){
            case DOWN:
                currentFrame = walkDownAnimation.getKeyFrame(frameTime);
                break;
            case LEFT:
                currentFrame = walkLeftAnimation.getKeyFrame(frameTime);
                break;
            case RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(frameTime);
                break;
            case UP:
                currentFrame = walkUpAnimation.getKeyFrame(frameTime);
                break;
            default:
                break;
        }
    }

    public void setBoundingBoxSize(float percentageWidthReduced, float percentageHeightReduced){
        //update the current bounding box
        float width;
        float height;

        float widthReductionAmount = 1.0f - percentageWidthReduced; //0.8f for 20% (1 - 0.2f)
        float heightReductionAmount = 1.0f - percentageHeightReduced; // the same

        if( widthReductionAmount > 0 && widthReductionAmount < 1){
            width = FRAME_WIDTH * widthReductionAmount;
        } else {
            width = FRAME_WIDTH;
        }

        if( heightReductionAmount > 0 && heightReductionAmount < 1){
            height = FRAME_WIDTH * heightReductionAmount;
        } else {
            height = FRAME_WIDTH;
        }

        if( width ==0 || height == 0 ){
            Gdx.app.debug(TAG, "Width and height are 0!! " + width + ":" + height);
        }

        //Need to account for the unitscale, since the map coordinates will be in pixels
        float minX;
        float minY;
        if ( MapManager.UNIT_SCALE > 0 ){
            minX = nextPlayerPosition.x/ MapManager.UNIT_SCALE;
            minY = nextPlayerPosition.y/ MapManager.UNIT_SCALE;
        } else {
            minX = nextPlayerPosition.x;
            minY = nextPlayerPosition.y;
        }

        boundingBox.set(minX, minY, width, height);
    }

    private void loadDefaultSprite(){
        Texture texture = Utility.getTextureAsset( defaultSpritePath);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        frameSprite = new Sprite(textureFrames[0][0].getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        currentFrame = textureFrames[0][0];
    }

    private void loadAllAnimations(){
        // Walking animation
        Texture texture = Utility.getTextureAsset( defaultSpritePath);
        TextureRegion[][] textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT);
        walkDownFrames = new Array<TextureRegion>();
        walkLeftFrames = new Array<TextureRegion>();
        walkRightFrames = new Array<TextureRegion>();
        walkUpFrames = new Array<TextureRegion>();

        for (int i =0; i< 4 ; i++){
            for (int j = 0; j< 4; j++){
                TextureRegion region = textureFrames[i][j];
                if( region == null){
                    Gdx.app.debug(TAG, "Got null animation frame " + i + "," + j);
                }
                switch (i){
                    case 0:
                        walkDownFrames.insert(j,region);
                        break;
                    case 1:
                        walkLeftFrames.insert(j,region);
                        break;
                    case 2:
                        walkRightFrames.insert(j,region);
                        break;
                    case 3:
                        walkUpFrames.insert(j,region);
                        break;
                }
            }
        }
        walkDownAnimation = new Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP);
        walkLeftAnimation = new Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP);
        walkRightAnimation = new Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP);
        walkUpAnimation = new Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP);
    }

    public void setState(State state){
        this.state = state;
    }

    public Sprite getFrameSprite(){
        return frameSprite;
    }

    public static EntityConfig getEntityConfig(String configFilePath) {
        Json json = new Json();
        return json.fromJson(EntityConfig.class, Gdx.files.internal(configFilePath));
    }

    static public Array<EntityConfig> getEntityConfigs(String configFilePath){
        Json json = new Json();
        Array<EntityConfig> configs = new Array<>();

        ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(configFilePath));

        for( JsonValue jsonVal: list){
            configs.add(json.readValue(EntityConfig.class, jsonVal));
        }

        return configs;
    }

    public EntityConfig getEntityConfig() {
        return entityConfig; //TODO
    }

    public void setEntityConfig(EntityConfig entityConfig) {
        this.entityConfig = entityConfig;
    }

    public void sendMessage(Component.MESSAGE messageType, String... args) {
        String fullMessage = messageType.toString();
        for( String string: args){
            fullMessage += Component.MESSAGE_TOKEN + string;
        }

        for (Component component: components){
            component.receiveMessage(fullMessage);
        }
    }



}

