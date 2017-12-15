package com.example.lucie.bejeweled;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedHashSet;

import static android.content.ContentValues.TAG;

/**
 * Created by Lucie on 10.12.2017.
 */

public class BejeweledView extends View {

    int lx = 8;
    int ly = 8;

    int width;
    int height;

    float startClickX;
    float startClickY;

    double fullScore = 0;

    int pocetTahu = 0;

    TextView mainScore;
    //private static MediaPlayer mediaPlayer;

    private int gameArr[][] = new int[8][8];
    public Bitmap[] symbols = null;


    int aaa = 0;

    public BejeweledView(Context context) {
        super(context);
        initView(context);
    }


    public BejeweledView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(symbols == null) {
            initView(context);
        }

    }

    public BejeweledView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    //inicializace
    public void initView(Context context){
        fillArray();
        initSymbols();
    }
    //naplnění s random symboly
    public void fillArray(){
        boolean check = true;
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                do{
                    gameArr[i][j] = (int) (Math.random() * 9);
                    check = checkArr(gameArr, i, j);
                }while(check == false);

            }
        }
    }
    //definice symbolů
    void initSymbols() {
        symbols = new Bitmap[9];

        symbols[0] = BitmapFactory.decodeResource(getResources(), R.drawable.blue);
        symbols[1] = BitmapFactory.decodeResource(getResources(), R.drawable.bluepent);
        symbols[2] = BitmapFactory.decodeResource(getResources(), R.drawable.bluerect);
        symbols[3] = BitmapFactory.decodeResource(getResources(), R.drawable.green);
        symbols[4] = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
        symbols[5] = BitmapFactory.decodeResource(getResources(), R.drawable.pink);
        symbols[6] = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
        symbols[7] = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        symbols[8] = BitmapFactory.decodeResource(getResources(), R.drawable.silver);
    }

    //určení velikosti symbolů
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w / ly;
        height = w / lx;
    }

    //vykreslení canvasu
    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < lx; i++) {
            for (int j = 0; j < ly; j++) {
                canvas.drawBitmap(symbols[gameArr[i][j]], null,
                        new Rect(j*width, i*height,(j+1)*width, (i+1)*height), null);
                //Log.d(TAG, "symbol: " + gameArr[i][j]);
            }
        }
        // invalidate();
    }

    //dialogové okno po vyčerpání všech možných tahů
    public void gameOver(){

        invalidate();
        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
        alertDialog.setTitle("No more moves");
        alertDialog.setMessage("Game over.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((BejeweledMain)getContext()).getBack();
                    }
                });
        alertDialog.show();

    }

    //zvuk pro hru, funkce volaná z BejeweledMain
    public void playSound(){

        ((BejeweledMain)getContext()).bejSound();

    }

    //on touch event, řešení swipování po telefonu, směry prohazování symbolů a volání funkcí pro kontrolu tahů
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        int smerx;
        int smery;

        //float endClickX;
        //float endClickY;

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            startClickX = e.getX();
            startClickY = e.getY();
            return true;
        } else if (e.getAction() != MotionEvent.ACTION_UP) {
            //Log.d(TAG, "hold");
            return true;
        }

        float endClickX = e.getX();
        float endClickY = e.getY();

        smerx = (int) Math.round(startClickX - endClickX);
        smery = (int) Math.round(endClickY - startClickY);


        int selectedSymbolSloupec = (int) (startClickX / width);
        int selectedSymbolRadek = (int) (startClickY / height);

        int selectedSymbolsSloupecEnd = (int) (endClickX / width);
        int selectedSymbolsRadekEnd = (int) (endClickY / height);


        if (selectedSymbolRadek >= 8 || selectedSymbolSloupec >=8) {
            return false;
        } else { //kliknuti v hernim poli
            if (selectedSymbolRadek == selectedSymbolsRadekEnd && selectedSymbolSloupec == selectedSymbolsSloupecEnd) {//nic neprovádět při provedení swipu po stejném symbolu
                return false;
            } else { //jinak zkoušet prohození
                if (Math.abs(smerx) > Math.abs(smery)) { //tahy po x ose
                    if (smerx < 0) { //smer doprava
                        if (selectedSymbolSloupec + 1 >= ly) { //neprohazovat mimo okraje pole
                            return true;
                        }
                        int pom = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                        gameArr[selectedSymbolRadek][selectedSymbolSloupec] = gameArr[selectedSymbolRadek][selectedSymbolSloupec + 1];
                        gameArr[selectedSymbolRadek][selectedSymbolSloupec + 1] = pom;

                        boolean correct = correctMove(gameArr); //kontrola platného tahu

                        if(correct != true){ //při neplatném tahu, zpětné prohození
                            int pom2 = gameArr[selectedSymbolRadek][selectedSymbolSloupec + 1];
                            gameArr[selectedSymbolRadek][selectedSymbolSloupec + 1] = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                            gameArr[selectedSymbolRadek][selectedSymbolSloupec] = pom2;
                        }else{ //posuny symbolů
                            playSound();

                            movesInArr(gameArr);

                            if(pocetTahu == 0) {

                                gameOver();
                            }
                            correct = false;
                        }
                        invalidate(); //překreslení

                    } else { //doleva
                        if (selectedSymbolSloupec - 1 < 0) { //neprohazovat mimo pole
                            return true;
                        }
                        int pom = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                        gameArr[selectedSymbolRadek][selectedSymbolSloupec] = gameArr[selectedSymbolRadek][selectedSymbolSloupec - 1];
                        gameArr[selectedSymbolRadek][selectedSymbolSloupec - 1] = pom;
                        //doleva

                        boolean correct = correctMove(gameArr);
                        if(correct != true){
                            int pom2 = gameArr[selectedSymbolRadek][selectedSymbolSloupec - 1];
                            gameArr[selectedSymbolRadek][selectedSymbolSloupec - 1] = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                            gameArr[selectedSymbolRadek][selectedSymbolSloupec] = pom2;
                        }else{
                            movesInArr(gameArr);
                            playSound();

                            if(pocetTahu == 0){

                                gameOver();
                            }
                            correct = false;
                        }

                        invalidate();
                    }
                } else {//po ose y
                    if (smery < 0) {
                        //nahoru
                        if (selectedSymbolRadek - 1 < 0) {
                            return true;
                        }
                        int pom = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                        gameArr[selectedSymbolRadek][selectedSymbolSloupec] = gameArr[selectedSymbolRadek - 1][selectedSymbolSloupec];
                        gameArr[selectedSymbolRadek - 1][selectedSymbolSloupec] = pom;

                        boolean correct = correctMove(gameArr);
                        if(correct != true){
                            int pom2 = gameArr[selectedSymbolRadek - 1][selectedSymbolSloupec];
                            gameArr[selectedSymbolRadek - 1][selectedSymbolSloupec] = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                            gameArr[selectedSymbolRadek][selectedSymbolSloupec] = pom2;
                        }else{
                            movesInArr(gameArr);
                            playSound();
                            if(pocetTahu == 0){
                                gameOver();
                            }
                            correct = false;
                        }
                        invalidate();
                    } else {//dolu
                        if (selectedSymbolRadek + 1 >= ly) {
                            return true;
                        }
                        int pom = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                        gameArr[selectedSymbolRadek][selectedSymbolSloupec] = gameArr[selectedSymbolRadek + 1][selectedSymbolSloupec];
                        gameArr[selectedSymbolRadek + 1][selectedSymbolSloupec] = pom;


                        boolean correct = correctMove(gameArr);
                        if(correct != true){
                            int pom2 = gameArr[selectedSymbolRadek + 1][selectedSymbolSloupec];
                            gameArr[selectedSymbolRadek + 1][selectedSymbolSloupec] = gameArr[selectedSymbolRadek][selectedSymbolSloupec];
                            gameArr[selectedSymbolRadek][selectedSymbolSloupec] = pom2;
                        }else{

                            movesInArr(gameArr);
                            playSound();
                            if(pocetTahu == 0){
                             //   onFinishInflate();
                                gameOver();
                            }
                            correct = false;
                        }

                        invalidate();
                    }
                }
                startClickX = -1;
                startClickY = -1;
                return true;
            }
        }
    }

    //kontrola vytvoření trojice v poli po tahu
    public boolean correctMove(int[][] arr){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr.length; j++){
                if(i>1){
                    if(arr[i][j] == arr[i-1][j] && arr[i][j] == arr[i-2][j]){
                        return true;
                    }
                }
                if(j>1){
                    if(arr[i][j] == arr[i][j-1] && arr[i][j] == arr[i][j-2]){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //kontrola, zda je vytvořena nějaká trojice v poli
    public int moveExist(int[][] arr){
        int p = 0;
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr.length; j++){
                if(i>1){
                    if(arr[i][j] == arr[i-1][j] && arr[i][j] == arr[i-2][j]){
                        return p = -1;
                    }
                }
                if(j>1){
                    if(arr[i][j] == arr[i][j-1] && arr[i][j] == arr[i][j-2]){
                        return p = -1;
                    }
                }
            }
        }
        return p;
    }

    //výpočet možných tahů v poli
    public int numbersOfMoves(int[][] arr){
        int tahy = 0;
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr.length; j++){

                if(i < 7){
                    int pom = arr[i][j];
                    arr[i][j] = arr[i+1][j];
                    arr[i+1][j] = pom;
                    int pocet = moveExist(arr);

                    if(pocet != 0){
                        tahy++;
                    }

                    int pom2 = arr[i+1][j];
                    arr[i+1][j] = arr[i][j];
                    arr[i][j] = pom2;
                }

                if(j < 7){
                    int pom = arr[i][j];
                    arr[i][j] = arr[i][j+1];
                    arr[i][j+1] = pom;
                    int pocet = moveExist(arr);

                    if(pocet != 0){
                        tahy++;
                    }

                    int pom2 = arr[i][j+1];
                    arr[i][j+1] = arr[i][j];
                    arr[i][j] = pom2;
                }
            }
        }
        return tahy;
    }

    //kontrola při vytváření pole, aby nevznikaly rovnou trojice+
    public boolean checkArr(int[][] arr, int i, int j){
        if (i>1){
            if(arr[i][j] == arr[i-1][j] && arr[i][j] == arr[i-2][j]){
                return false;
            }
        }
        if(j>1){
            if(arr[i][j] == arr[i][j-1] && arr[i][j] == arr[i][j-2]){
                return false;
            }
        }
        return true;
    }

    //uložení stejných symbolů v řadě do hash setu
    public void addToHashSet(int[][] arr, LinkedHashSet<Point> pole){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr.length; j++){
                if(i>1){
                    if(arr[i][j] == arr[i-1][j] && arr[i][j] == arr[i-2][j]){
                        Point p = new Point(i, j);
                        Point p1 = new Point(i-1, j);
                        Point p2 = new Point(i-2, j);

                        pole.add(p2);
                        pole.add(p1);
                        pole.add(p);
                    }
                }
                if(j>1){
                    if(arr[i][j] == arr[i][j-1] && arr[i][j] == arr[i][j-2]){
                        Point p = new Point(i, j);
                        Point p1 = new Point(i, j-1);
                        Point p2 = new Point(i, j-2);

                        pole.add(p2);
                        pole.add(p1);
                        pole.add(p);
                    }
                }
            }
        }
    }


    //posuny v poli dolů,  při odstranění stejných symbolů
    private void movesInArr(int[][] arr){
        int max = 8;
        int min = 0;
        int range = (max - min) + 1;


        LinkedHashSet<Point> pole = new LinkedHashSet<>();
        addToHashSet(arr, pole);

        int pocetPoli = pole.size();
        int skore = pocetPoli*20;
        if(pocetPoli > 3){
            skore += pocetPoli*10;
        }
        fullScore += skore;

        mainScore = (TextView) ((Activity) getContext()).findViewById( R.id.myScore );

        mainScore.setText("Score: " + (long)Math.floor(fullScore));

        for(Point p : pole){
            for(int i = p.x; i > 0; i--){
                arr[i][p.y] = arr[i-1][p.y];
            }
            arr[0][p.y] = (int)(Math.random() * 9);
        }

        for(int k = 0; k < arr.length; k++){
            for(int l = 0; l <arr.length; l++){
                if(k>1){
                    if(arr[k][l] == arr[k-1][l] && arr[k][l] == arr[k-2][l]){
                        movesInArr(arr);
                    }
                }
                if(l>1){
                    if(arr[k][l] == arr[k][l-1] && arr[k][l] == arr[k][l-2]){
                        movesInArr(arr);
                    }
                }
            }
        }
        pocetTahu = numbersOfMoves(arr);
    }





}
