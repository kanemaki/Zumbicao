package com.example.zumbicao;

import java.util.ArrayList;
import java.util.Arrays;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.JumpModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.SurfaceGestureDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.ease.EaseBounceOut;
import org.andengine.util.modifier.ease.EaseExponentialOut;

import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity {
	private static final int LARGURA = 800;
	private static final int ALTURA = 480;

	private TiledTextureRegion regiaoMadrugaCorrendo;
	private TiledTextureRegion regiaoMadrugaAtirando;
	private TiledTextureRegion regiaoMadrugaRecarregando;
	private TiledTextureRegion regiaoZicaCorrendo;
	private TiledTextureRegion regiaoZumbi;
	private AutoParallaxBackground background;
	private ITextureRegion fundoFixo;
	private ITextureRegion fundoMovimento;
	private AnimatedSprite madrugaCorrendo, madrugaAtirando, madrugaRecarregando, zicaCorrendo, zumbi;
	private Scene cena;
	
	
	private ITextureRegion regiaoBala;
	private int auto = 5;
	boolean rodando;
	private int tiros = 3;
	
	private ArrayList<IShape> listaDeColisoes = new ArrayList<IShape>();

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, LARGURA, ALTURA);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(LARGURA, ALTURA), camera);
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		mEngine.enableVibrator(this);
		/*** Segundo dia - Inicio ***/
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlas textura = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_8888);
		textura.load();
		this.fundoFixo = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textura, this, "background.png", 0, 188);
		
		textura = new BitmapTextureAtlas(this.getTextureManager(), 512, 1024, BitmapTextureFormat.RGBA_8888);
		textura.load();

		this.fundoMovimento = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textura, this, "trilho.png", 0, 0);
		
		background = new AutoParallaxBackground(0, 0, 0, auto);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(0, ALTURA - this.fundoFixo.getHeight(), this.fundoFixo, getVertexBufferObjectManager())));
		
		ParallaxEntity trilho = new ParallaxEntity(-10.0f, new Sprite(0, ALTURA - this.fundoMovimento.getHeight(), this.fundoMovimento, getVertexBufferObjectManager()));
		background.attachParallaxEntity(trilho);
		
		textura = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		textura.load();
		regiaoMadrugaCorrendo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textura, this, "madruga-correndo.png", 0, 0, 8, 1);
		
		final float playerX = (LARGURA - (regiaoMadrugaCorrendo.getWidth() + 200)) / 2;
		final float playerY = ALTURA - regiaoMadrugaCorrendo.getHeight() - 2;
		
		madrugaCorrendo = new AnimatedSprite(playerX, playerY, regiaoMadrugaCorrendo, getVertexBufferObjectManager());
		madrugaCorrendo.setScaleCenterY(regiaoMadrugaCorrendo.getHeight());
		madrugaCorrendo.setScale(2);
		
		long[] frames = new long[4];
	    Arrays.fill(frames,100);
		madrugaCorrendo.animate(frames, 0,3, true);

		/*** Segundo dia - Fim ***/
		
			
		/* Terceiro dia - Inicio */
		textura = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		textura.load();
		regiaoMadrugaAtirando =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textura, this, "madruga-atirando.png", 0, 0, 2, 1);
		madrugaAtirando = new AnimatedSprite(madrugaCorrendo.getX(), madrugaCorrendo.getY() + 10, regiaoMadrugaAtirando,  getVertexBufferObjectManager());
		
		textura = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		textura.load();
		regiaoMadrugaRecarregando =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textura, this, "madruga-recarregando.png", 0, 0, 9, 2);
		madrugaRecarregando = new AnimatedSprite(madrugaCorrendo.getX(), madrugaCorrendo.getY() + 10, regiaoMadrugaRecarregando, getVertexBufferObjectManager());

		textura = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		textura.load();
		regiaoZicaCorrendo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textura, this, "zica-correndo.png", 0 , 0, 8, 1);
		zicaCorrendo = new AnimatedSprite(playerX - 200, playerY + 10, regiaoZicaCorrendo, getVertexBufferObjectManager());
		
		frames = new long[3];
		Arrays.fill(frames,200);
		zicaCorrendo.animate(frames, 3, 5, true);
		
		textura = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		textura.load();
		regiaoZumbi = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textura, this, "zumbis.png", 0, 0, 10, 11);
		
		textura = new BitmapTextureAtlas(this.getTextureManager(), 32,32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		textura.load();
		regiaoBala = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textura, this, "8-bit-bullet.jpg", 0, 0);
		/* Terceiro dia - FIM */
		
	}
	
	//Habilitar a deteccao de gestos na cena. (onfling)
	private void detectarGestos(){
		SurfaceGestureDetector surfaceGestureDetector = new SurfaceGestureDetector(this) {
			
			@Override
			protected boolean onSwipeUp() {
				
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						madrugaCorrendo.registerEntityModifier(new JumpModifier(0.5f, madrugaCorrendo.getX(), madrugaCorrendo.getX(), madrugaCorrendo.getY(), madrugaCorrendo.getY(), 60));
					}});
				
				
				return true;
			}
			
			@Override
			protected boolean onSwipeRight() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected boolean onSwipeLeft() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected boolean onSwipeDown() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected boolean onSingleTap() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			protected boolean onDoubleTap() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		
		surfaceGestureDetector.setEnabled(true);
		cena.setOnSceneTouchListener(surfaceGestureDetector);
		
		
		
	}
	
	
	@Override
	public Scene onCreateScene() {
		cena = new Scene();
		cena.setBackground(background);
		cena.attachChild(madrugaCorrendo);

		cena.attachChild(zicaCorrendo);
		
		final TimerHandler timer = new TimerHandler(3, true, new ITimerCallback() {  
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				
				
				Zumbi zumbi = new Zumbi(LARGURA - 150, ALTURA - regiaoMadrugaCorrendo.getHeight() - 2, regiaoZumbi, getVertexBufferObjectManager());
				zumbi.setScaleCenterY(zumbi.getHeight());
				zumbi.setScale(2);

				cena.attachChild(zumbi);
				cena.registerTouchArea(zumbi);
				
				listaDeColisoes.add(zumbi);
				}
			});
		timer.setAutoReset(true);
		getEngine().registerUpdateHandler(timer);
		runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				detectarGestos();				// TODO Auto-generated method stub
				
			}
		});
		return cena;
	}
	
	
	
class Zumbi extends AnimatedSprite{
	PhysicsHandler fisicaHandler;
	
	public Zumbi(float pX, float pY, 
		ITiledTextureRegion pTiledTextureRegion,
		VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, 
				vertexBufferObjectManager);
		 fisicaHandler = new PhysicsHandler(this);
		 registerUpdateHandler(fisicaHandler);
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		atirar();
		return super.onAreaTouched(pSceneTouchEvent, 
				pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	@Override
	public void onAttached() {
		long[] frames = new long[9];
		Arrays.fill(frames, 170);//100 ms para cada frame
		animate(frames, 30, 38, true); //animando em loop 
		
		fisicaHandler.setVelocityX(-30);
		
		
		
	}
}

	
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	public void atirar(){
		if(!rodando){
			rodando = true;
			cena.detachChild(madrugaCorrendo);

			cena.attachChild(madrugaAtirando);
			
			long[] frames = new long[2];
		    Arrays.fill(frames,200);
		    
			madrugaAtirando.animate(frames, 0, 1, false, new IAnimationListener() {
				
				@Override
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
						int pInitialLoopCount) {
					rodando = true;
					background.setParallaxChangePerSecond(0);
				}
				
				@Override
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
						int pRemainingLoopCount, int pInitialLoopCount) {
				}
				
				@Override
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
						int pOldFrameIndex, int pNewFrameIndex) {
					
					Log.i("Game", "Old " + pOldFrameIndex + " -  New " + pNewFrameIndex + " Total " + pAnimatedSprite.getTileCount());
					if(pNewFrameIndex == (pAnimatedSprite.getTileCount() - 1)){
						
						Log.i("Game", "Estou no ultimo frame");
						
						float y = (ALTURA - madrugaAtirando.getHeight()) - 10;
						
						
						final Bala bala = new Bala(madrugaCorrendo.getX() + madrugaCorrendo.getWidth(), y, regiaoBala, getVertexBufferObjectManager());
						
						bala.registerUpdateHandler(new IUpdateHandler(){
							@Override
							public void onUpdate(float pSecondsElapsed) {
								for(IShape zumbi : listaDeColisoes){
									if(bala.collidesWith(zumbi)){
										final Zumbi z = (Zumbi)zumbi;
										
										runOnUpdateThread(new Runnable(){
	
											@Override
											public void run() {
												cena.unregisterTouchArea(z);
//												z.dispose();
//												z.detachSelf();
												if(bala != null){
													bala.dispose();
													bala.detachSelf();	
												}
												
												long[] frames = new long[33];
												Arrays.fill(frames, 170);
												z.animate(frames, 63, 95, false, new IAnimationListener() {
													
													@Override
													public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
															int pInitialLoopCount) {
														listaDeColisoes.remove(z);
														
													}
													
													@Override
													public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
															int pRemainingLoopCount, int pInitialLoopCount) {
														// TODO Auto-generated method stub
														
													}
													
													@Override
													public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
															int pOldFrameIndex, int pNewFrameIndex) {
														// TODO Auto-generated method stub
														
													}
													
													@Override
													public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
														
														runOnUpdateThread(new Runnable(){

															@Override
															public void run() {
																z.detachSelf(); 
																
															}});
														
														
													}
												});
												
												
												
												mEngine.vibrate(10);
												
											}}, true);
										
									}
								}
							}
							@Override
							public void reset() {}});
						
						
						cena.attachChild(bala);
						
						
					}
					
				}
				
				@Override
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					background.setParallaxChangePerSecond(auto);
					--tiros;
					
					 
					if(tiros <= 0){
						rodando = false;
						recarregar();
					}else{
						cena.detachChild(madrugaAtirando);
						cena.attachChild(madrugaCorrendo);
					}
					
					
					rodando = false;
				}
			});
		}
	}
	
	private void recarregar(){
		if(!rodando){
			rodando = true;
			cena.detachChild(madrugaAtirando);
			cena.attachChild(madrugaRecarregando);
			madrugaRecarregando.setScaleCenterY(madrugaRecarregando.getHeight());
			madrugaRecarregando.setScale(2);
			
			long[] frameDurations = new long[9];
		    Arrays.fill(frameDurations,200);
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    
		    madrugaRecarregando.animate(frameDurations, 0, 8, false, new IAnimationListener() {
				
				@Override
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
						int pInitialLoopCount) {
					rodando = true;
					background.setParallaxChangePerSecond(0);
					zicaCorrendo.stopAnimation(0);
				}
				
				@Override
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
						int pRemainingLoopCount, int pInitialLoopCount) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
						int pOldFrameIndex, int pNewFrameIndex) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					background.setParallaxChangePerSecond(auto);
					long[] frames = new long[3];
					Arrays.fill(frames,200);
					zicaCorrendo.animate(frames, 3, 5, true);

					
					tiros = 3;
					cena.detachChild(madrugaRecarregando);
					cena.attachChild(madrugaCorrendo);
					rodando = false;
				}
			});
		}
	}

	
}



