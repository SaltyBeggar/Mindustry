package io.anuke.mindustry.input;

import static io.anuke.mindustry.Vars.*;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.scene.utils.Cursors;
import io.anuke.ucore.util.Tmp;

public enum PlaceMode{
	cursor{
		public void draw(int tilex, int tiley, int endx, int endy){
			float x = tilex * Vars.tilesize;
			float y = tiley * Vars.tilesize;
			
			boolean valid = world.validPlace(tilex, tiley, player.recipe.result) && (android || control.getInput().cursorNear());

			Vector2 offset = player.recipe.result.getPlaceOffset();

			float si = MathUtils.sin(Timers.time() / 6f) + 1;

			Draw.color(valid ? Color.PURPLE : Color.SCARLET);
			Draw.thickness(2f);
			Draw.linecrect(x + offset.x, y + offset.y, tilesize * player.recipe.result.width + si, tilesize * player.recipe.result.height + si);

			player.recipe.result.drawPlace(tilex, tiley, player.rotation, valid);
			Draw.thickness(2f);

			if(player.recipe.result.rotate){
				Draw.color(Color.ORANGE);
				Tmp.v1.set(7, 0).rotate(player.rotation * 90);
				Draw.line(x, y, x + Tmp.v1.x, y + Tmp.v1.y);
			}
			
			if(valid)
				Cursors.setHand();
			else
				Cursors.restoreCursor();
		}
	}, 
	touch{
		{
			lockCamera = true;
		}
		
		public void draw(int tilex, int tiley, int endx, int endy){
			
			float t = Vars.tilesize;
			
			float x = tilex * t, y = tiley * t, 
					x2 = endx * t, y2 = endy * t;
			
			if(x2 < x){
				float d = x;
				x = x2;
				x2 = d;
			}
			
			if(y2 < y){
				float d = y;
				y = y2;
				y2 = d;
			}
			
			if(x2 >= x){
				x -= t/2;
				x2 += t/2;
			}
			
			if(y2 >= y){
				y -= t/2;
				y2 += t/2;
			}
			
			if(tilex == endx && tiley == endy){
				cursor.draw(tilex, tiley, endx, endy);
			}else{
				Draw.color(Color.GREEN);
				Draw.linerect(x, y, x2 - x, y2 - y);
				Draw.alpha(0.3f);
				Draw.crect("blank", x, y, x2 - x, y2 - y);
				Draw.reset();
			}
		}
		
		public void tapped(int tilex, int tiley, int endx, int endy){
			if(endx < tilex){
				int t = endx;
				endx = tilex;
				tilex = t;
			}
			if(endy < tiley){
				int t = endy;
				endy = tiley;
				tiley = t;
			}
			
			for(int x = tilex; x <= endx; x ++){
				for(int y = tiley; y <= endy; y ++){
					control.getInput().tryPlaceBlock(x, y);
				}
			}
			
		}
	},
	breaker{
		public void draw(int tilex, int tiley, int endx, int endy){
			Tile tile = world.tile(tilex, tiley);
			
			if(tile != null && world.validBreak(tilex, tiley)){
				if(tile.isLinked())
					tile = tile.getLinked();
				Vector2 offset = tile.block().getPlaceOffset();
				float fract = player.breaktime / tile.getBreakTime();
				
				if(Inputs.buttonDown(Buttons.RIGHT)){
					Draw.color(Color.YELLOW, Color.SCARLET, fract);
					Draw.linecrect(tile.worldx() + offset.x, tile.worldy() + offset.y, tile.block().width * Vars.tilesize, tile.block().height * Vars.tilesize);
				}else if(android && player.breaktime > 0){
					Draw.color(Color.YELLOW, Color.SCARLET, fract);
					Draw.circle(tile.worldx(), tile.worldy(), 4 + (1f - fract) * 26);
				}
				Draw.reset();
			}
		}
	};
	public boolean lockCamera;
	
	public void draw(int tilex, int tiley, int endx, int endy){
		
	}
	
	public void tapped(int tilex, int tiley, int endx, int endy){
		
	}
}
