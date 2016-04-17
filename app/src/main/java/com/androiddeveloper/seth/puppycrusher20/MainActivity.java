package com.androiddeveloper.seth.puppycrusher20;
//http://developer.android.com/guide/topics/ui/layout/gridview.html
//staring point and cite

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    // Global Variables
    private int selectedPosition = -1;
    private int changingPosition = -1;
    private View selectedView = null;
    private View changingView = null;
    private long selectedId = -1;
    private long changingId = -1;
    private ImageView selectedImage = null;
    private ImageView changingImage = null;
    private Integer placeholder = null;
    private final int[][] row = {{0, 1, 2, 3}, {4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}, {16, 17, 18, 19}};
    private final int[][] col = {{0, 4, 8, 12, 16}, {1, 5, 9, 13, 17}, {2, 6, 10, 14, 18}, {3, 7, 11, 15, 19}};
    private Object temp;
    private int score = 0;

    ArrayList<Integer> matchedRow = new ArrayList<>();
    ArrayList<Integer> matchedCol = new ArrayList<>();
    private Integer[] uniqueThumb = {R.drawable.sample_0, R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5, R.drawable.sample_6, R.drawable.sample_7};


    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        final TextView info = (TextView) findViewById(R.id.info);
        final TextView scoreboard = (TextView) findViewById(R.id.score);
        scoreboard.setText("Score: " + score);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // If nothing is selected, set selectedPosition
                if (selectedPosition == -1) {
                    selectedPosition = position;
                    selectedView = v;
                    selectedId = id;
                    selectedImage = (ImageView) v;
                    // If same position was selected, de-select it
                } else if (selectedPosition == position) {
                    resetSelection();
                    // Else, select changingPosition and switch images if allowed
                } else {
                    changingPosition = position;
                    changingView = v;
                    changingId = id;
                    changingImage = (ImageView) v;
                    int diff = changingPosition - selectedPosition;
                    //switch images only if adjacent
                    if (diff == -4 || diff == 4 || diff == -1 || diff == 1) {
                        placeholder = mThumbIds[changingPosition];
                        changingImage.setImageResource(mThumbIds[selectedPosition]);
                        selectedImage.setImageResource(placeholder);
                        mThumbIds[changingPosition] = mThumbIds[selectedPosition];
                        mThumbIds[selectedPosition] = placeholder;
                        resetSelection();

                        //check three in a row
                        // CALL THE METHOD
                        if (lineChecker(position)) {
                            score += 100;
                            scoreboard.setText("Score: " + score);
                            Toast.makeText(MainActivity.this, "100 Points!", Toast.LENGTH_SHORT).show();

                            replaceLines(gridview);

                        }

                        // If images are not adjacent, reset selection
                    } else {
                        resetSelection();
                        scoreboard.setText("Score: " + score);
                    }
                }
                //to assist in creation
                //info.setText("Selected: " + selectedPosition + "  Changing: " + changingPosition);
                //Toast.makeText(MainActivity.this, "" + position,
                //    Toast.LENGTH_SHORT).show();
            }
        });

    }


    private Integer getRandamDraw() {
        int num;
        Random ran = new Random();
        num = ran.nextInt(8);

        return uniqueThumb[num];
    }


    private void replaceLines(GridView g) {


        if (!matchedRow.isEmpty()) {
            for (int i = 0; i < matchedRow.size(); i++) {
                ImageView iv = (ImageView) g.getChildAt(matchedRow.get(i));
                iv.setImageResource(R.drawable.tn_blood);
                mThumbIds[matchedRow.get(i)] = R.drawable.tn_blood;
                //the idea was to delay do that a blood splat would appear and then be replaced using the line below though was not a useful solution
               // SystemClock.sleep(1000);
            }

            for (int i = 0; i < matchedRow.size(); i++) {
                ImageView iv = (ImageView) g.getChildAt(matchedRow.get(i));
                Integer newDraw = getRandamDraw();
                iv.setImageResource(newDraw);
                mThumbIds[matchedRow.get(i)] = newDraw;
            }
        }
        //  just col match
        if (!matchedCol.isEmpty()) {
            for (int i = 0; i < matchedCol.size(); i++) {
                ImageView iv = (ImageView) g.getChildAt(matchedCol.get(i));
                iv.setImageResource(R.drawable.tn_blood);
                mThumbIds[matchedCol.get(i)] = R.drawable.tn_blood;
              //  SystemClock.sleep(1000);

            }

            for (int i = 0; i < matchedCol.size(); i++) {
                ImageView iv = (ImageView) g.getChildAt(matchedCol.get(i));
                Integer newDraw = getRandamDraw();
                iv.setImageResource(newDraw);
                mThumbIds[matchedCol.get(i)] = newDraw;
            }
        }

    }

    //check method
    private boolean lineChecker(int pos) {
        boolean match = false;

        int[] rowCheckArray = row[getRow(pos)];
        int[] colCheckArray = col[getCol(pos)];
        if (rowCheck(rowCheckArray) || colCheck(colCheckArray)) {
            match = true;
        }


        return match;
    }

    //rowcheck method
    private boolean rowCheck(int[] arr) {
        boolean line = false;
        int count = 1;
        Integer currentId = mThumbIds[arr[0]];
        Integer secondId = null;

        for (int i = 1; i < arr.length; i++) {
            secondId = mThumbIds[arr[i]];
            if (currentId.equals(secondId)) {
                count++;
                if (!matchedRow.contains(arr[i - 1])) {
                    matchedRow.add(arr[i - 1]);
                }

                matchedRow.add(arr[i]);

                if (count >= 3) {
                    line = true;
                }
            } else {
                if (!line) {
                    count = 1;
                    matchedRow.clear();
                }
            }
            currentId = secondId;
        }
        if (!line)
            matchedRow.clear();
        return line;
    }

    //colcheck method
    private boolean colCheck(int[] arr) {
        boolean line = false;
        int count = 1;
        Integer currentId = mThumbIds[arr[0]];
        Integer secondId = null;

        for (int i = 1; i < arr.length; i++) {
            secondId = mThumbIds[arr[i]];
            if (currentId.equals(secondId)) {
                count++;
                if (!matchedCol.contains(arr[i - 1])) {
                    matchedCol.add(arr[i - 1]);
                }

                matchedCol.add(arr[i]);
                if (count >= 3) {
                    line = true;
                }
            } else {
                if (!line) {
                    count = 1;
                    matchedCol.clear();
                } else {
                    i = arr.length;
                }
            }
            currentId = secondId;
        }
        if (!line)
            matchedCol.clear();
        return line;

    }


    //row method
    private int getRow(int pos) {
        int ret = -1;
        for (int i = 0; i < row.length; i++) {
            int[] inner = row[i];
            for (int x = 0; x < inner.length; x++) {
                if (inner[x] == pos) {
                    ret = i;
                }
            }

        }
        return ret;
    }


    //col method
    private int getCol(int pos) {
        int reter = -1;
        for (int i = 0; i < col.length; i++) {
            int[] inner = col[i];
            for (int x = 0; x < inner.length; x++) {
                if (inner[x] == pos) {
                    reter = i;
                }
            }
        }
        return reter;
    }

    private void resetSelection() {
        selectedPosition = -1;
        selectedView = null;
        selectedId = -1;
        selectedImage = null;
        changingPosition = -1;
        changingView = null;
        changingId = -1;
        changingImage = null;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }


    }
}