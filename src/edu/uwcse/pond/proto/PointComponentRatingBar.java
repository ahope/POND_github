package edu.uwcse.pond.proto;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.proto.client.MyRequestFactory;
import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RatingBar;


public class PointComponentRatingBar extends RatingBar {
	
	private PointComponent mPointComponent; 

	public PointComponentRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PointComponentRatingBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public PointComponentRatingBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setPointComponent(PointComponent pc){
		mPointComponent = pc;
	}

	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//if (mPointComponent.equals(PointComponent.FATS)){
			//setProgressDrawable(getResources().getDrawable(edu.uwcse.pond.proto.R.drawable.pt_rating_bar_fats));
		//}
		Drawable fats = getResources().getDrawable(edu.uwcse.pond.proto.R.drawable.pt_rating_bar_fats);
		Drawable other = getProgressDrawable();
		fats.setBounds(other.getBounds());
		setProgressDrawable(fats);
		super.onDraw(canvas);
		
	}
}
