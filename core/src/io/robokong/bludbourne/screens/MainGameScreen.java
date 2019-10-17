package io.robokong.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Json;

import io.robokong.bludbourne.*;

public class MainGameScreen implements Screen {
	private static final String TAG = MainGameScreen.class.getSimpleName();
	private  static final int VIEWPORT_WIDTH = 10;
	private  static final int VIEWPORT_HEIGHT = 10;

	private static class VIEWPORT {
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}

	private OrthogonalTiledMapRenderer _mapRenderer = null;
	private OrthographicCamera _camera = null;
	//private static MapManager _mapMgr;
	private Json _json;
//	private Map currentMap;

	public MainGameScreen(){
		_json = new Json();
	}

//	public void loadMap(MapFactory.MapType mapType){
//		Map map = MapFactory.getMap(mapType);
//		if( map == null ){
//			Gdx.app.debug(TAG, "Map does not exist!  ");
//			return;
//		}
//		currentMap = map;
//		Gdx.app.debug(TAG, "Player Start: (" + currentMap.getPlayerStart().x + "," + currentMap.getPlayerStart().y + ")");
//	}

	private Entity _player;

	@Override
	public void show() {
		MapFactory.setCamera(_camera);
		MapFactory.getMap(MapFactory.MapType.TOWN);

		//_camera setup
		setupViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

		//get the current size
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

		_mapRenderer = new OrthogonalTiledMapRenderer(MapFactory.getCurrentMap().getCurrentTiledMap(), Map.UNIT_SCALE);
		_mapRenderer.setView(_camera);

		MapFactory.setCamera(_camera);

		Gdx.app.debug(TAG, "UnitScale value is: " + _mapRenderer.getUnitScale());

		_player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER);
		MapFactory.getCurrentMap().setPlayer(_player);
	}

	@Override
	public void hide() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_mapRenderer.setView(_camera);

		//_mapRenderer.getBatch().enableBlending();
		//_mapRenderer.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if( MapFactory.getCurrentMap().hasMapChanged() ){
			_mapRenderer.setMap(MapFactory.getCurrentMap().getCurrentTiledMap());
			_player.sendMessage(Component.MESSAGE.INIT_START_POSITION, _json.toJson(MapFactory.getCurrentMap().getPlayerStartUnitScaled()));

			_camera.position.set(MapFactory.getCurrentMap().getPlayerStartUnitScaled().x, MapFactory.getCurrentMap().getPlayerStartUnitScaled().y, 0f);
			_camera.update();

			MapFactory.getCurrentMap().setMapChanged(false);
		}

		_mapRenderer.render();

		MapFactory.getCurrentMap().updateMapEntities(_mapRenderer.getBatch(), delta );

		_player.update(MapFactory.getCurrentMap(), _mapRenderer.getBatch(), delta);
	}



	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		_player.dispose();
		_mapRenderer.dispose();
	}

	private void setupViewport(int width, int height){
		//Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		//Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		//pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		//aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		//update viewport if there could be skewing
		if( VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio){
			//Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		}else{
			//letterbox above and below
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer: virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")" );
		Gdx.app.debug(TAG, "WorldRenderer: physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")" );
	}
}
