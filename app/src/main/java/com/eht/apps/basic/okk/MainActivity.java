package com.eht.apps.basic.okk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eht.apps.basic.okk.wordprediction.Word;
import com.eht.apps.basic.okk.wordprediction.WordPredictEngine;
import com.eht.apps.basic.okk.xmlparser.Entry;
import com.eht.apps.basic.okk.xmlparser.OkkXmlParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    private static final String TAG = "One Key Keyboard (okk) > ";

    public Timer charTimer;
    public TimerTask myTimerTask;
    private boolean timerIsRunning = false;
    private boolean capsLockIsOn = false;
    public Intent tmrsvcintent;
    long timeSwapBuff = 0L;
    long updatedTime = 800L;
    public static Long timerDelay = 3000L;
    private List<String> alphabets;
    private List<String> numbers;
    private List<String> otherChars;
    private List<String> finalArrayOfChars;
    protected static String fontfilepath = "res/DejaVuSans.ttf";
    protected static String dicfile = "englishUK/brit-a-z.txt";
    protected static String charmapfilepath = "english.xml";
    private Word previousWord, currentWord, nextWord;
    protected static boolean wordPredictionFeatureEnabled = true;
    private boolean wordPredictionIsActive = false;
    public static int predictedWordsCount = 8;
    private List<String> predictedWords = new ArrayList<String>();
    public static List<String> EngUkWordList;
    private int ctr = 0;
    private int ctr1 = 0;
    private Menu optmenu1;
    protected static int txtareafontsize;
    protected static int btnfontsize1;
    protected static int btnfontsize2;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadDefaultValuesFromSharedPreferances();

        ReturnUnicodeStringsFromIntRanges(charmapfilepath);

        changeFont(fontfilepath);

        if (charmapfilepath.endsWith("english.xml")){

            InputStream is;

            try {
                is = getAssets().open(dicfile);
                EngUkWordList = WordPredictEngine.putWordsIntoArrayList(is);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        Button mainbtn = (Button) findViewById(R.id.button1);
        Button btnStart = (Button) findViewById(R.id.button2);
        Button btnCaps = (Button) findViewById(R.id.button3);
        Button btnDirection = (Button) findViewById(R.id.button4);
        Button btnBkSp = (Button) findViewById(R.id.button5);

        EditText edittext1 = (EditText) findViewById(R.id.editText1);

        InputMethodManager inpmtdmgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inpmtdmgr.hideSoftInputFromWindow(edittext1.getWindowToken(), 0);

        edittext1.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.valueOf(txtareafontsize));
        mainbtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,btnfontsize2);

        mainbtn.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnCaps.setOnClickListener(this);
        btnDirection.setOnClickListener(this);
        btnBkSp.setOnClickListener(this);

        edittext1.setOnClickListener(this);


    }

    //@Override
    protected void ooStart() {
        super.onStart();
        LoadDefaultValuesFromSharedPreferances();
        //UpdateUIandOtherComponents();
    }

    @Override
    public void onResume() {
        super.onResume();
        //LogSharedPreferenceValues();

        LoadDefaultValuesFromSharedPreferances();
        LogSharedPreferenceValues();
        UpdateUIandOtherComponents();

    }

    private void LogSharedPreferenceValues() {

        LogMessage("Delay = "+ String.valueOf(timerDelay));
        LogMessage("Textarea fontsize = " + String.valueOf(txtareafontsize));
        LogMessage("Btn Multichar fontsize = " + String.valueOf(btnfontsize2));
        LogMessage("WordPred Enabled = " + String.valueOf(wordPredictionFeatureEnabled));
        LogMessage("WordPred Word Count = "+ String.valueOf(predictedWordsCount));
        LogMessage("Language Fils = " + charmapfilepath);

    }

    private void UpdateUIandOtherComponents() {

        if (timerDelay != null && timerIsRunning == true) {
            RestartTimerWithNewDelay(timerDelay);
        }

        Button maintbtn = (Button) findViewById(R.id.button1);
        maintbtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,btnfontsize2);

        EditText edtxt = (EditText) findViewById(R.id.editText1);
        edtxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,txtareafontsize);
    }

    private void RestartTimerWithNewDelay(Long tmplong1) {

        timerIsRunning = true;

        StartOrStopTimer();

        timerDelay = tmplong1;

        timerIsRunning = false;

        StartOrStopTimer();
    }

    protected void onPause() {
        super.onPause();

    }

    private void LoadDefaultValuesFromSharedPreferances() {
        // TODO Auto-generated method stub

        SharedPreferences shpref = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            wordPredictionFeatureEnabled = shpref.getBoolean("wordprediction_checkbox", true);

            predictedWordsCount = new Integer(Integer.parseInt(shpref.getString("wordpred_count", "10")));

            txtareafontsize = new Integer(Integer.parseInt(shpref.getString("app_fontsize_txtarea", "32")));

            btnfontsize1 = new Integer(Integer.parseInt(shpref.getString("app_fontsize_btn_singlechar", "64")));

            btnfontsize2 = new Integer(Integer.parseInt(shpref.getString("app_fontsize_btn_multichar", "32")));

            charmapfilepath = new String(shpref.getString("app_language_file", charmapfilepath));

            InputStream ishq = getAssets().open(charmapfilepath);

            List<String> fns = OkkXmlParser.getLanguageAttributeFileName(ishq);

            fontfilepath = new String(fns.get(0));	// font file
            dicfile = new String(fns.get(1));	// dict file

            String tmplongstr1 = shpref.getString("character_delay", "9000");
            Long tmplong0 = new Long( Long.parseLong(tmplongstr1, 10));

            if (tmplong0 != null)
                timerDelay = tmplong0;
        }
        catch (NumberFormatException nfex) {
            nfex.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (wordPredictionFeatureEnabled == false) {
            dicfile = new String("");
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        boolean startbtnclicked = false;
        int viewid = new Integer(v.getId());

        switch (viewid) {

            case R.id.button1:
                typeText();
                break;
            case R.id.button2:
                // start / stop
                startbtnclicked = true;
                try {
                    StartOrStopTimer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button3:
                // set Caps on / off
                try {
                setCapsLock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button4:
                try {
                ReverseOrderOfChars();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button5:
                // Backspace function
                backSpacePressed();
                break;

            case R.id.editText1:
                GetWordNearCurrentCursorPosition();

            default:
                break;

        };

        if (startbtnclicked == true) {
            Button startbtn = (Button) findViewById(R.id.button2);
            MenuItem item = optmenu1.findItem(R.id.item1);
            if (timerIsRunning == false) {
                startbtn.setText("Start");
                item.setChecked(false);
                setMainButtonTextAfterStoppingTimer();
            } else {
                startbtn.setText("Stop");
                item.setChecked(true);
            }
        }

    }

    private void GetWordNearCurrentCursorPosition() {
        // TODO Auto-generated method stub
        EditText et1 = (EditText)findViewById(R.id.editText1);

        currentWord = new Word();

        int currcursorpos = et1.getSelectionStart();

        int startcurorpos = currcursorpos - 1;
        while (startcurorpos  >= 0) {
            CharSequence cq = et1.getText().subSequence(startcurorpos, startcurorpos +1);
            String chr = cq.toString();

            if ( chr.equals(" ") || chr.equals("\t") ||  chr.equals("\n") ) {
                CharSequence chrseq = currentWord.getSpelling();
                String chrseqlist = chrseq.toString();
                StringBuilder builder = new StringBuilder(chrseqlist);
                builder.reverse();
                chrseqlist = builder.toString();
                chrseq = chrseqlist;
                currentWord.setSpelling(chrseq);
                break;
            }
            else {
                currentWord.AddCharToSpelling(chr);
            }

            startcurorpos--;
        }

    }

    private void ReverseOrderOfChars() {
        // reverse order of chars

        Button ReverseOrderbtn = (Button) findViewById(R.id.button4);
        String RevBtnText = ReverseOrderbtn.getText().toString();

        RevBtnText = RevBtnText.equals("Bkwd")? "Fwd" : "Bkwd";

        ReverseOrderbtn.setText(RevBtnText);

        try {
            String curchar = finalArrayOfChars.get(ctr);
            Collections.reverse(finalArrayOfChars);
            ctr = finalArrayOfChars.indexOf(curchar);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ctr < 0)
            ctr = 0;

        if (ctr > finalArrayOfChars.size())
            ctr = finalArrayOfChars.size();
    }

    private void setCapsLock() {
        // TODO Auto-generated method stub

        Button btnCaps = (Button) findViewById(R.id.button3);

        if (capsLockIsOn) {
            capsLockIsOn = false;
            btnCaps.setText("ABC");
        }
        else {
            capsLockIsOn = true;
            btnCaps.setText("abc");
        }
    }

    private void typeText() {
        // TODO Auto-generated method stub

        Button mbtn = (Button) findViewById(R.id.button1);
        String btnText = mbtn.getText().toString();

        if (btnText.equals("Press Menu -> Start.....")) {
            btnText = "";
        }
        if (btnText.toUpperCase().equals("SPACE") || btnText.equals("Space")) {
            btnText = " ";
        }
        if (btnText.toUpperCase().equals("TAB") || btnText.equals("Tab")) {
            btnText = "\t";
        }
        if (btnText.toUpperCase().equals("RETURN") || btnText.equals("Return")) {
            btnText = "\n";
        }

        EditText edittext = (EditText) findViewById(R.id.editText1);

        int cursorPosition = edittext.getSelectionEnd();
        CharSequence enteredText = edittext.getText().toString();
        CharSequence startToCursor = enteredText.subSequence(0,
                cursorPosition);
        CharSequence cursorToEnd = enteredText.subSequence(
                cursorPosition, enteredText.length());

        if (currentWord == null)
            currentWord = new Word();

        if (capsLockIsOn) {
            btnText = btnText.toUpperCase();
        }
        else {
            btnText = btnText.toLowerCase();
        }

        if (btnText.toString().length() == 1) {
            currentWord.AddCharToSpelling(btnText);
        }

        int currWordLength = currentWord.getSpelling().toString().length();
        if (currWordLength >= 2) {

            if (dicfile.equals("")) {
                wordPredictionIsActive = false;
            }
            else {
                wordPredictionIsActive = true;

//                try {
                    predictedWords = new ArrayList<String>();
                    predictedWords = WordPredictEngine.findWordsStartingWith(currentWord.getSpelling().toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

            if (predictedWords.isEmpty()){
                ctr1 = 0;
                wordPredictionIsActive = false;
            }
        }

        if (capsLockIsOn) {
            btnText = btnText.toUpperCase();
        }
        else {
            btnText = btnText.toLowerCase();
        }

        if (btnText.endsWith(" ") || btnText.endsWith("\t") || btnText.endsWith("\n")) {
            if (btnText.length() > 1) {
                currentWord.setName(currentWord.getSpelling().toString());
                currentWord.setLen(currentWord.getName().length());

                btnText = CamelCase(btnText, currentWord);

                startToCursor = startToCursor.subSequence(0, (startToCursor.length() - currWordLength));

                wordPredictionIsActive = false;
                ctr1 = 0;
                previousWord = new Word();
                previousWord = currentWord;
                currentWord = new Word();
            } else {
                wordPredictionIsActive = false;
                ctr1 = 0;
                currentWord = new Word();
            }
        }

        String outstr = startToCursor.toString() + btnText.toString() + cursorToEnd;

        try {
            edittext.setText(outstr);
        }
        catch (Exception ex ) {
            ex.printStackTrace();
        }

        String res = edittext.getText().toString();

        if (!res.isEmpty() && btnText.toString().length() == 1)
            edittext.setSelection(++cursorPosition);
        else if (!res.isEmpty() && btnText.toString().length() > 1) {
            cursorPosition = startToCursor.length();
            int newCursorPos = cursorPosition + btnText.toString().length();
            edittext.setSelection(newCursorPos);
        }

    }



    private String CamelCase(String btnText, Word currentWord2) {
        // TODO Auto-generated method stub

        CharSequence ch1 = currentWord2.getSpelling();
        int ch1len = ch1.length();
        CharSequence ch0 = btnText.subSequence(0, ch1len);

        btnText=btnText.replace(ch0, ch1);

        return btnText;
    }



    private void backSpacePressed() {
        // TODO Auto-generated method stub

        EditText edittext = (EditText)findViewById(R.id.editText1);

        int cursorPosition = edittext.getSelectionEnd();
        try {
            CharSequence enteredText = edittext.getText().toString();
            CharSequence startToCursor = enteredText.subSequence(0,
                    cursorPosition);
            CharSequence cursorToEnd = enteredText.subSequence(
                    cursorPosition, enteredText.length());

            currentWord.RemoveLastCharFromSpelling();
            if (cursorPosition >= 1) {
                String outstr = startToCursor.subSequence(0, cursorPosition - 1)
                        .toString();

                outstr = outstr + cursorToEnd.toString();
                edittext.setText(outstr);
            }
            edittext.setSelection(cursorPosition-1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }




    private void changeFont(String fontfilepath) {
        EditText tv = (EditText) findViewById(R.id.editText1);
        Button bv = (Button) findViewById(R.id.button1);
        Typeface font = Typeface.createFromAsset(getAssets(), fontfilepath);
        tv.setTypeface(font);
        bv.setTypeface(font);

    }

    private void ReturnUnicodeStringsFromIntRanges(String charmapfilepath) {

        try {

            InputStream fl = getAssets().open(charmapfilepath);
            XmlPullParser okkxp = OkkXmlParser.newOkkXmlParser(fl);

            Entry e1 = new Entry();
            e1 = OkkXmlParser.ControlFlowThroughXmlMethod(okkxp, e1, "Alphabets");


            Entry e2 = new Entry();
            // okkxp.setEntry(e2);
            e2 = OkkXmlParser.ControlFlowThroughXmlMethod(okkxp, e2, "Numbers");
            // e2 = okkxp.getEntry();

            InputStream fl1 = getAssets().open("english.xml");
            XmlPullParser okkxp1 = OkkXmlParser.newOkkXmlParser(fl1);

            Entry e3 = new Entry();
            // okkxp.setEntry(e3);
            e3 = OkkXmlParser.ControlFlowThroughXmlMethod(okkxp1, e3, "OtherChars");
            // e3 = okkxp.getEntry();

            int start = e1.getIncludeRange_Start();
            int end = e1.getIncludeRange_End();
            alphabets = getChars(e1);
            //Log.d(TAG, "alphabets - start: " + start + "  end: " + end);

            start = e2.getIncludeRange_Start();
            end = e2.getIncludeRange_End();
            numbers = getChars(e2);
            //Log.d(TAG, "numbers - start: " + start + "  end: " + end);

            otherChars = getChars(e3);
            /*Log.d(TAG, "otherChars - start: " + e3.getIncludeRange_Start()
                    + "  end: " + e3.getIncludeRange_End());
            for (int a = 0; a < e3.getExculdeRange_Start().size(); a++) {
                Log.d(TAG, "otherChars - Exclude_start: "
                        + e3.getExculdeRange_Start().get(a) + "  Exclude_end: "
                        + e3.getExculdeRange_End().get(a));
            }*/

        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finalArrayOfChars = new ArrayList<String>();
        addExtrasToFinalArrayOfStrings();
        finalArrayOfChars.addAll(alphabets);

        // logs...........
        /*Log.d(TAG, "alphabets : ");
        for (String s : alphabets) {
            Log.d(s.toString(), " ");
        }
        Log.d(TAG, "numbers : ");
        for (String s : numbers) {
            Log.d(s.toString(), " ");
        }
        Log.d(TAG, "otherChars : ");
        for (String s : otherChars) {
            Log.d(" ", s.toString());
        }*/
    }

    private void addExtrasToFinalArrayOfStrings() {
        // put some extra strings in alphabets.....
        alphabets.add("Space");
        alphabets.add("Tab");
        alphabets.add("Return");
    }

    private List<String> getChars(Entry e3) {
        // TODO Auto-generated method stub

        List<String> lstr = new ArrayList<String>();

        UnicodeStringHelper ush = new UnicodeStringHelper();

        Set<Integer> excludedChars = new HashSet<Integer>();

        if (!e3.getExculdeRange_Start().isEmpty()) {

            for (int i = e3.getIncludeRange_Start(); i <= e3
                    .getIncludeRange_End(); i++) { // include range

                for (int j = 0; j < e3.getExculdeRange_Start().size(); j++) {
                    // no.	of exclude ranges

                    for (int k = e3.getExculdeRange_Start().get(j); k <= e3
                            .getExculdeRange_End().get(j); k++) {
                        // each	element in exclude range
                        if (i != k) {
                            excludedChars.add(k);
                        }
                    }
                }
            }

            List<Integer> master = new ArrayList<Integer>();

            for (int i = e3.getIncludeRange_Start(); i <= e3
                    .getIncludeRange_End(); i++) {
                master.add(i);
            }

            for (int l : master) {
                if (!excludedChars.contains(l)) {
                    lstr.add(ush.ConvertIntToUnicodeString(l).toString());
                }
            }

        } else {

            int start = e3.getIncludeRange_Start();
            int end = e3.getIncludeRange_End();

            lstr = getChars(start, end);
        }

        return lstr;
    }

    private List<String> getChars(int start, int end) {
        // TODO Auto-generated method stub

        List<String> retstrlst = new ArrayList<String>();
        UnicodeStringHelper ush = new UnicodeStringHelper();

        for (int i = start; i <= end; i++) {
            String s = ush.ConvertIntToUnicodeString(i);
            retstrlst.add(s);
        }

        return retstrlst;

    }

    private void LogMessage(String msgstr) {
        Log.d("okk -> log.d : ", msgstr);
    }

    private List<String> GetUnicodeStrings(Entry e1) {
        // TODO Auto-generated method stub

        List<String> lstr = new ArrayList<String>();

        UnicodeStringHelper ush = new UnicodeStringHelper();

        if (!e1.getExculdeRange_Start().isEmpty()
                && !e1.getExculdeRange_End().isEmpty()) {

            for (int i = e1.getIncludeRange_Start(); i <= e1
                    .getIncludeRange_End(); i++) {

                for (int j = 0; j < e1.getExculdeRange_Start().size(); j++) {

                    for (int k = e1.getExculdeRange_Start().get(j); k <= e1
                            .getExculdeRange_End().get(j); k++) {

                        if (i != k) {
                            lstr.add(ush.ConvertIntToUnicodeString(k));
                        }
                    }
                }
            }
        }
        return lstr;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.okkmainmenu1, menu);

        this.optmenu1 = menu; 
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // View view = (View) item;

        Button stbtn = (Button) findViewById(R.id.button2);

        switch (item.getItemId()) {
            case R.id.item1:
                StartOrStopTimer();
                break;
            case R.id.item16:
                bringUpDelayDialog();
                break;
            case R.id.item18:
                SettingsActivityLaunch();
                break;
            case R.id.item17:
                AboutDialog();
                break;
            case R.id.item3:
                changeDirectionOnCharSetChange();
                setAlpha();
                break;
            case R.id.item4:
                changeDirectionOnCharSetChange();
                setNumb();
                break;
            case R.id.item5:
                changeDirectionOnCharSetChange();
                setOthChars();
                break;
            case R.id.item6:
                changeDirectionOnCharSetChange();
                setAlphaNumb();
                break;
            case R.id.item7:
                changeDirectionOnCharSetChange();
                setAlphaOthChars();
                break;
            case R.id.item8:
                changeDirectionOnCharSetChange();
                setNumbOthChars();
                break;
            case R.id.item9:
                changeDirectionOnCharSetChange();
                setAllCharecters();
                break;
            case R.id.item11:
                sendToDialer();
                break;
            case R.id.item12:
                sendToEmail();
                break;
            case R.id.item13:
                sendToBrowser();
                break;
            case R.id.item14:
                sendToNotes();
                break;
            case R.id.item15:
                sendToCustomApp();
                break;

        };

        if (timerIsRunning == true) {
            item.setChecked(true);
            stbtn.setText("Stop");
        } else {
            item.setChecked(false);
            stbtn.setText("Start");
            setMainButtonTextAfterStoppingTimer();
        }

        return true;
    }



    private void changeDirectionOnCharSetChange() {
        Button b4 = (Button) findViewById(R.id.button4);

        String btn4txt = b4.getText().toString();

        if (btn4txt.equals("Bkwd")) {
            b4.setText("Fwd");
        } else {
            b4.setText("Bkwd");
        }
    }

    @SuppressLint("NewApi")
    private void sendToCustomApp() {
        // TODO Auto-generated method stub
        // showAlertDialog("Info", "This Feature not yet Implemented.....");

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);

        // Always use string resources for UI text.
        // This says something like "Share this photo with"
        String title = getResources().getString(R.string.app_chooser_title);
        // Create intent to show chooser
        Intent chooser = Intent.createChooser(mainIntent, title);

        EditText et1 = (EditText) findViewById(R.id.editText1);
        String extras = et1.getText().toString();

        chooser.putExtra(extras, title);

        // Verify the intent will resolve to at least one activity
        if (mainIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    public void showAlertDialog(String title, String message) {

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void sendToNotes() {
        // TODO Auto-generated method stub

        EditText edtxt = (EditText) findViewById(R.id.editText1);
        String notetxt = edtxt.getText().toString();

        Calendar myCalender = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String formattedDate = df.format(myCalender.getTime());

        String name = Environment.getExternalStorageDirectory().getPath() + "/"
                + formattedDate.toString() + ".txt";

        writeToFile(name, notetxt);

    }

    public void writeToFile(String filename, String data) {

        try {
            File myFile = new File(filename);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(getBaseContext(), "Done writing SD " + filename,
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void sendToBrowser() {
        // TODO Auto-generated method stub

        EditText edtxt = (EditText) findViewById(R.id.editText1);
        String gotourl = edtxt.getText().toString();

        Uri currurl = Uri.parse(gotourl);

        if (URLUtil.isValidUrl(gotourl)) {
            Intent browserintent = new Intent(Intent.ACTION_VIEW, currurl);

            startActivity(browserintent);
        } else {
            String url = "http://www.google.com/#q=";
            String query = gotourl.toString().trim();
            String final_url = url + query;

            Uri uri = Uri.parse(final_url);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }

    }

    private void sendToEmail() {
        // TODO Auto-generated method stub
        String subject = "";
        EditText edtxt = (EditText) findViewById(R.id.editText1);
        String body = edtxt.getText().toString();

        Intent emailintent = new Intent(Intent.ACTION_SEND);
        emailintent.setType("text/html");

        emailintent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { "" });
        emailintent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailintent.putExtra(Intent.EXTRA_TEXT, body);

        emailintent.setType("message/rfc822");

        startActivity(Intent.createChooser(emailintent,
                "Choose an Email client"));

    }

    private void sendToDialer() {
        // TODO Auto-generated method stub

        EditText edtxt = (EditText) findViewById(R.id.editText1);
        String phnumb = edtxt.getText().toString();

        try {
            Long pn = Long.parseLong(phnumb,10);

            phnumb = String.valueOf(pn);

            Intent dialerintent = new Intent(Intent.ACTION_DIAL);

            dialerintent.setData(Uri.parse("tel:" + phnumb));

            startActivity(dialerintent);

        } catch (NumberFormatException nfex) {
            nfex.printStackTrace();
            showAlertDialog("Invalid Format...", "No Phone number entered OR Entered Phone number not in valid format.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void setAllCharecters() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(alphabets);
        finalArrayOfChars.addAll(numbers);
        finalArrayOfChars.addAll(otherChars);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void setNumbOthChars() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(numbers);
        finalArrayOfChars.addAll(otherChars);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void setAlphaOthChars() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(alphabets);
        finalArrayOfChars.addAll(otherChars);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void setAlphaNumb() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(alphabets);
        finalArrayOfChars.addAll(numbers);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void setOthChars() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(otherChars);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void setNumb() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(numbers);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void setAlpha() {
        // TODO Auto-generated method stub
        finalArrayOfChars = new ArrayList<String>();
        finalArrayOfChars.addAll(alphabets);

        addExtrasToFinalArrayOfStrings();

        setCtrToZero();

        Log.d(TAG, "final Chars : ");
        for (String s : finalArrayOfChars) {
            Log.d(" ", s.toString());
        }
    }

    private void AboutDialog() {
        // TODO Auto-generated method stub

        showAlertDialog("About okk [one key keyboard]", "Developed by......\n"
                + "Prashanth L. Kamath.\n" + "[(C) Mindtree Ltd.]");
    }

    private void SettingsActivityLaunch() {
        // TODO Auto-generated method stub
        //showAlertDialog("Info", "This Feature not yet Implemented.....");

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void StartOrStopTimer() {
        // TODO Auto-generated method stub

        if (timerIsRunning == false) {
            // Start

            timerIsRunning = true;

            charTimer = new Timer();
            charTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timerOnExecuteMethod();
                }

            }, 0, timerDelay);

        } else {
            // Stop

            timerIsRunning = false;

            charTimer.cancel();

            Button btn = (Button) findViewById(R.id.button1);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,btnfontsize2);

        }

    }



    private void setMainButtonTextAfterStoppingTimer() {

        setCtrToZero();

        Button b = (Button) findViewById(R.id.button1);
        b.setText(R.string.buttonString);
    }

    public void timerOnExecuteMethod() {

        this.runOnUiThread(Timer_Tick);

    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {

            // This method runs in the same thread as the UI.
            // Do something to the UI thread here

            try {
                // Your code
                if (wordPredictionIsActive == false) {
                    if (ctr < finalArrayOfChars.size()) {
                        String btonString = finalArrayOfChars.get(ctr);
                        Button b = (Button) findViewById(R.id.button1);
                        b.setTextSize(TypedValue.COMPLEX_UNIT_SP,btnfontsize1);
                        if (capsLockIsOn == false)
                            b.setText(btonString);
                        else
                            b.setText(btonString.toUpperCase());
                        ctr++;
//						if (currentWord.getSpelling().toString().length() >= 2) {
//							wordPredictionIsActive = true;
//						}
                    } else {
                        setCtrToZero();
                    }
                } else if (wordPredictionIsActive == true) {
                    if (ctr1 < predictedWords.size()) {
                        String btonString = predictedWords.get(ctr1);
                        Button b = (Button) findViewById(R.id.button1);
                        b.setTextSize(TypedValue.COMPLEX_UNIT_SP,btnfontsize2);
                        b.setText(btonString);
                        ctr1++;
                        if (ctr1 >= predictedWords.size()) {
                            ctr1 =0;
                            wordPredictionIsActive = false;
                        }
                    } else {
                        if (ctr1 >= predictedWords.size()) {
                            ctr1 =0;
                            wordPredictionIsActive = false;
                        }
                    }

                }

            } catch (Exception e) {
                // TODO: handle exception
                Log.e(e.getMessage().toString(), null, e);
            }

        }

    };

    private void setCtrToZero() {
        ctr = 0;
    }

    private void bringUpDelayDialog() {
        // TODO Auto-generated method stub

        String message = "Enter Delay in ms.......";

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        String tempinputvalue = String.valueOf(timerDelay);
        input.setText(tempinputvalue);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delay [in ms]")
                .setMessage(message)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable editable = input.getText();
                        // deal with the editable

                        String dlgtxt = editable.toString();
                        java.lang.Long tmplong1 = 0L;

                        try {
                            tmplong1 = Long.parseLong(dlgtxt);
                        } catch (NumberFormatException nfex) {
                            nfex.printStackTrace();
                        }
                        if (tmplong1 != 0L) {
                            if (tmplong1 <= 500L) {
                                showAlertDialog("Alert....", "Delay entered might be too less. If Delay < 500ms - iteration of chars might be too fast.");
                                timerDelay = tmplong1;
                            }
                            if (timerIsRunning == true) {
                                RestartTimerWithNewDelay(tmplong1);
                            } else {
                                timerDelay = tmplong1;
                            }

                            SharedPreferences.Editor preference = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            //preference.putInt("character_delay",Integer.valueOf(String.valueOf(timerDelay), 10));
                            preference.putString("character_delay", String.valueOf(Integer.valueOf(String.valueOf(timerDelay), 10)));
                            preference.commit();

                        } else
                            showAlertDialog("Enter valid Delay...", "Enter only Numbers with no Spaces / Special Chars.");
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // Do nothing.
                            }
                        }).show();



    }


}

