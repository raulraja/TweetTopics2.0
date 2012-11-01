package com.javielinux.tweettopics2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.google.android.maps.*;

import java.util.ArrayList;
import java.util.List;

public class MapViewer extends MapView {
    
	private float mLatitude;
	private float mLongitude;
	private boolean move = false;
	private float x = 0;
	private float y = 0;
	private boolean canCreateElements = true;

    public MapViewer(Context context, String key) {
        super(context, key);
    }

    public MapViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void createMarker(double latitude, double longitude) {
        canCreateElements = false;
        placeMarker((int)(latitude*1E6), (int)(longitude*1E6));
    }


	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (canCreateElements) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				move = false;
				x = ev.getX();
				y = ev.getY();
			}
			if (ev.getAction() == MotionEvent.ACTION_MOVE) {
				float difX = Math.abs(x - ev.getX());
				float difY = Math.abs(y - ev.getY());
				if ( (difX>5) && (difY>5) ) move = true;
			}
			if ( (ev.getAction() == MotionEvent.ACTION_UP) && !move ) {
				Projection proj = this.getProjection();
				GeoPoint loc = proj.fromPixels((int)ev.getX(), (int)ev.getY());
				mLongitude = (float)loc.getLongitudeE6()/1000000;
				mLatitude = (float)loc.getLatitudeE6()/1000000;
				placeMarker(loc.getLatitudeE6(), loc.getLongitudeE6());
			}
		}
		return super.onTouchEvent(ev);
	}
	
	private void placeMarker(int markerLatitude, int markerLongitude) {
		if (this.getOverlays().size()>0) this.getOverlays().remove(0);
		Drawable marker=getResources().getDrawable(R.drawable.bubble);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
		marker.getIntrinsicHeight());
		this.getOverlays().add(new InterestingLocations(marker,	markerLatitude, markerLongitude));
	}
	

	public float getLongitude() {
		return mLongitude;
	}

	public float getLatitude() {
		return mLatitude;
	}

	class InterestingLocations extends ItemizedOverlay<OverlayItem>{

		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Bitmap marker;
		private Bitmap markerShadow;
		private OverlayItem myOverlayItem;

		boolean MoveMap;
		GeoPoint myPlace;

		public InterestingLocations(Drawable defaultMarker,	int LatitudeE6, int LongitudeE6) {
			super(defaultMarker);
			marker = BitmapFactory.decodeResource(getResources(),R.drawable.bubble);
			markerShadow = BitmapFactory.decodeResource(getResources(),R.drawable.bubble_shadow);
			myPlace = new GeoPoint(LatitudeE6,LongitudeE6);
			myOverlayItem = new OverlayItem(myPlace, "My Place", "My Place");
			locations.add(myOverlayItem);

			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return locations.get(i);
		}

		@Override
		public int size() {
			return locations.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {		
			Point p = new Point();
			mapView.getProjection().toPixels(myPlace, p);
			
	    	if (shadow) {
	    		canvas.drawBitmap(markerShadow, p.x, p.y - markerShadow.getHeight(),null);
	    	} else {
				canvas.drawBitmap(marker, p.x -marker.getWidth()/2, p.y -marker.getHeight(),null);
	    	}

		}
	}
	
}