package io.robokong.bludbourne;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import io.robokong.bludbourne.Entity.AnimationType;

public class EntityConfig {

    Array<AnimationConfig> animationConfig;
    Entity.State state = Entity.State.IDLE;
    Entity.Direction direction = Entity.Direction.DOWN;
    String entityID;

    public EntityConfig() {
        this.animationConfig = new Array<>();
    }

    public Array<AnimationConfig> getAnimationConfig() {
        return animationConfig;
    }

    public void addAnimationConfig(AnimationConfig animationConfig) {
        this.animationConfig.add(animationConfig);
    }

    public Entity.State getState() {
        return state;
    }

    public void setState(Entity.State state) {
        this.state = state;
    }

    public Entity.Direction getDirection() {
        return direction;
    }

    public void setDirection(Entity.Direction direction) {
        this.direction = direction;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    static public class AnimationConfig{
        private float frameDuration = 1.0f;
        private AnimationType animationType;
        private Array<String> texturePath;
        private Array<GridPoint2> gridPoints;

        public AnimationConfig() {
            animationType = AnimationType.IDLE;
            texturePath = new Array<>();
            gridPoints= new Array<>();
        }

        public float getFrameDuration() {
            return frameDuration;
        }

        public void setFrameDuration(float frameDuration) {
            this.frameDuration = frameDuration;
        }

        public AnimationType getAnimationType() {
            return animationType;
        }

        public void setAnimationType(AnimationType animationType) {
            this.animationType = animationType;
        }

        public Array<String> getTexturePath() {
            return texturePath;
        }

        public void setTexturePath(Array<String> texturePath) {
            this.texturePath = texturePath;
        }

        public Array<GridPoint2> getGridPoints() {
            return gridPoints;
        }

        public void setGridPoints(Array<GridPoint2> gridPoints) {
            this.gridPoints = gridPoints;
        }
    }

}
