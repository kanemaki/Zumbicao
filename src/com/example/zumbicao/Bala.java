package com.example.zumbicao;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bala extends Sprite implements IEntity{

	
	PhysicsHandler fisicaHandler;
	
	
	public Bala(float pX, float pY,
			ITextureRegion regiaoBala,
			VertexBufferObjectManager vertexBufferObjectManager) {
		super(pX, pY, regiaoBala, vertexBufferObjectManager);
		
		 fisicaHandler = new PhysicsHandler(this);
		 registerUpdateHandler(fisicaHandler);
	}
	
	
	@Override
	public void onAttached() {
		fisicaHandler.setVelocityX(500);
	}
	
}
