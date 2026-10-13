package com.playeverywhere.ornamentgenerator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.GradientDrawable;
import java.util.Random;

public class MainActivity extends Activity {
    OrnamentView ornament;
    TextView speedLabel;
    int bg = Color.rgb(20, 23, 31);
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18),dp(22),dp(18),dp(12));
        root.setBackgroundColor(bg);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText("ОРНАМЕНТ");
        title.setTextColor(Color.WHITE); title.setTextSize(26); title.setTypeface(null,1);
        root.addView(title);
        TextView sub = new TextView(this);
        sub.setText("Генератор случайных узоров");
        sub.setTextColor(Color.rgb(164,174,194)); sub.setTextSize(14);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,-2); sp.bottomMargin=dp(18);
        root.addView(sub,sp);

        ornament = new OrnamentView(this);
        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(-1,0,1f);
        vp.bottomMargin=dp(16);
        root.addView(ornament,vp);

        TextView hint = new TextView(this);
        hint.setText("Лента движется непрерывно • нажми «Новый узор»");
        hint.setTextColor(Color.rgb(164,174,194)); hint.setTextSize(12);
        hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1,-2); hp.bottomMargin=dp(12);
        root.addView(hint,hp);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button random = new Button(this); random.setText("✦  Новый узор");
        random.setAllCaps(false); random.setTextColor(Color.WHITE);
        random.setBackground(round(Color.rgb(104,91,230),14));
        row.addView(random,new LinearLayout.LayoutParams(0,dp(54),1f));
        Button play = new Button(this); play.setText("Ⅱ");
        play.setTextSize(20); play.setTextColor(Color.WHITE); play.setAllCaps(false);
        play.setBackground(round(Color.rgb(43,49,65),14));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(dp(64),dp(54)); bp.leftMargin=dp(10);
        row.addView(play,bp);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1,-2); rp.bottomMargin=dp(12);
        root.addView(row,rp);
        random.setOnClickListener(v -> ornament.newPattern());
        play.setOnClickListener(v -> { boolean running=ornament.toggle(); play.setText(running ? "Ⅱ" : "▶"); });

        LinearLayout speedRow = new LinearLayout(this);
        speedRow.setGravity(Gravity.CENTER_VERTICAL);
        speedLabel = new TextView(this); speedLabel.setText("СКОРОСТЬ"); speedLabel.setTextColor(Color.rgb(164,174,194)); speedLabel.setTextSize(11);
        speedRow.addView(speedLabel);
        SeekBar speed = new SeekBar(this); speed.setMax(100); speed.setProgress(35);
        LinearLayout.LayoutParams seekp = new LinearLayout.LayoutParams(0,dp(42),1f); seekp.leftMargin=dp(10);
        speedRow.addView(speed,seekp); root.addView(speedRow);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar s,int p,boolean u){ ornament.setSpeed(0.25f+p*0.09f); }
            public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}
        });
        ornament.setSpeed(3.4f);
    }
    int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+0.5f);}
    GradientDrawable round(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}

    class OrnamentView extends View {
        Paint p = new Paint(3); Random random = new Random();
        int[] colors = {Color.rgb(244,184,92),Color.rgb(236,101,113),Color.rgb(93,205,190),Color.rgb(172,139,255),Color.rgb(246,222,153)};
        int base = Color.rgb(35,39,53);
        float tile=120f, offset=0, speed=3.4f;
        boolean running=true;
        int motif=0;
        OrnamentView(Activity a){super(a);setLayerType(View.LAYER_TYPE_SOFTWARE,null);setBackground(round(base,18));newPattern();}
        void setSpeed(float s){speed=s;}
        boolean toggle(){running=!running;invalidate();return running;}
        void newPattern(){motif=random.nextInt(5);base=Color.rgb(25+random.nextInt(30),25+random.nextInt(30),38+random.nextInt(35));tile=90+random.nextInt(65);for(int i=0;i<colors.length;i++) colors[i]=Color.rgb(80+random.nextInt(176),80+random.nextInt(176),80+random.nextInt(176));invalidate();}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c); float w=getWidth(),h=getHeight();
            c.drawColor(base);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.2f);p.setColor(Color.argb(45,255,255,255));
            c.drawRect(10,10,w-10,h-10,p);
            c.save(); c.clipRect(10,10,w-10,h-10);
            float cell=tile; float center=h/2f;
            for(float x=-cell-(offset%cell);x<w+cell;x+=cell) drawTile(c,x,center,cell,h);
            c.restore();
            if(running){offset+=speed;if(offset>tile)offset-=tile;postInvalidateDelayed(30);}
        }
        void drawTile(Canvas c,float x,float cy,float s,float h){
            int col=colors[(int)(Math.abs(Math.round((x+offset)/Math.max(1,tile)))%colors.length)];
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.2f);p.setColor(col);
            float half=s*.43f, y1=Math.max(20,cy-h*.34f), y2=Math.min(h-20,cy+h*.34f);
            c.drawLine(x,cy-half,x+half,cy,p);c.drawLine(x+half,cy,x,cy+half,p);c.drawLine(x,cy+half,x-half,cy,p);c.drawLine(x-half,cy,x,cy-half,p);
            p.setStyle(Paint.Style.FILL);p.setColor(col);
            switch(motif){
                case 0:
                    c.drawCircle(x,cy,s*.12f,p); c.drawCircle(x,cy-half,s*.065f,p); c.drawCircle(x,cy+half,s*.065f,p);
                    break;
                case 1:
                    p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawCircle(x,cy,s*.2f,p);c.drawCircle(x,cy,s*.32f,p);
                    break;
                case 2:
                    Path star=new Path();for(int i=0;i<10;i++){double a=-Math.PI/2+i*Math.PI/5;float r=(i%2==0)?s*.28f:s*.12f;float px=x+(float)Math.cos(a)*r,py=cy+(float)Math.sin(a)*r;if(i==0)star.moveTo(px,py);else star.lineTo(px,py);}star.close();c.drawPath(star,p);
                    break;
                case 3:
                    c.drawRoundRect(x-s*.18f,cy-s*.18f,x+s*.18f,cy+s*.18f,s*.05f,s*.05f,p);
                    break;
                default:
                    c.drawCircle(x,cy,s*.08f,p);c.drawCircle(x-s*.22f,cy,s*.05f,p);c.drawCircle(x+s*.22f,cy,s*.05f,p);
            }
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.6f);p.setColor(Color.argb(180,255,255,255));
            c.drawLine(x,cy-half*.5f,x+half*.5f,cy,p);c.drawLine(x+half*.5f,cy,x,cy+half*.5f,p);
            p.setColor(Color.argb(80,255,255,255));c.drawLine(x,y1,x,y2,p);
        }
    }
}
