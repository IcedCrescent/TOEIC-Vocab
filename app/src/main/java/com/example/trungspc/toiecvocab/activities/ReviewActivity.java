package com.example.trungspc.toiecvocab.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;
import com.example.trungspc.toiecvocab.databases.models.WordModel;
import com.example.trungspc.toiecvocab.utils.CommonConst;
import com.example.trungspc.toiecvocab.utils.HelperClass;
import com.example.trungspc.toiecvocab.utils.TextToSpeechInitializer;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;

public class ReviewActivity extends AppCompatActivity implements TextToSpeechInitializer.TextToSpeechIniListener {

    String TAG = "ReviewActivity";
    WordModel currentWord;
    int prevID = 1;
    Integer wordReviewCount;
    int wordsReviewedCounter;
    private TextToSpeechInitializer textToSpeechInitializer;
    private TextToSpeech  textToSpeech;
    @BindView(R.id.tvWord)
    TextView tvWord;
    @BindView(R.id.tv_pronounce1)
    TextView tvPronounce1;
    @BindView(R.id.iv_sound1)
    ImageView ivSound1;
    @BindView(R.id.guideline5)
    Guideline guideline5;
    @BindView(R.id.tvTapToReview)
    TextView tvTapToReview;
    @BindView(R.id.guideline6)
    Guideline guideline6;
    @BindView(R.id.guideline7)
    Guideline guideline7;
    @BindView(R.id.layoutQuestion)
    ConstraintLayout layoutQuestion;
    @BindView(R.id.tvNewWord)
    TextView tvNewWord;
    @BindView(R.id.tvPronunciation2)
    TextView tvPronunciation2;
    @BindView(R.id.tvMeaning)
    TextView tvMeaning;
    @BindView(R.id.tvLevel2)
    TextView tvLevel2;
    @BindView(R.id.iv_sound2)
    ImageView ivSound2;
    @BindView(R.id.tvIKnew)
    TextView tvIKnew;
    @BindView(R.id.tvNotKnown)
    TextView tvNotKnown;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.tvExampleEnglish)
    TextView tvExampleEnglish;
    @BindView(R.id.tvExampleVietnamese)
    TextView tvExampleVietnamese;
    @BindView(R.id.guideline8)
    Guideline guideline8;
    @BindView(R.id.layoutAnswer)
    ConstraintLayout layoutAnswer;
    @BindView(R.id.constraintLayout4)
    ConstraintLayout constraintLayout4;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up text to speech
        textToSpeechInitializer = new TextToSpeechInitializer(this, Locale.UK, this);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        wordReviewCount = sharedPreferences.getInt(CommonConst.WORD_REVIEW_COUNT, 1);
        loadData();
    }

    private void loadData() {
        int min = 1, max = DatabaseManager.getInstance(this).getListTopic().size();
        Random ran = new Random();
        int randomTopicId = ran.nextInt(min) + max;
        currentWord = DatabaseManager.getInstance(this).getRandomWord(randomTopicId, prevID);
        prevID = currentWord.getId();
        tvExampleEnglish.setText(currentWord.getExample());
        tvExampleVietnamese.setText(currentWord.getExampleTranslation());
        tvWord.setText(currentWord.getOrigin());
        tvNewWord.setText(currentWord.getOrigin());
        tvPronounce1.setText(currentWord.getPronunciation());
        tvPronunciation2.setText(currentWord.getPronunciation());
        tvMeaning.setText(currentWord.getExplanation());
        String level = HelperClass.getLevel(currentWord.getLevel());
        tvLevel2.setText(level);
        Picasso.get().load(currentWord.getImageUrl()).into(imageView2);
        wordsReviewedCounter++;
        if(canSpeak && sharedPreferences.getBoolean(CommonConst.PLAY_SOUND_AUTO, false)) {
            if (textToSpeech != null){
                textToSpeech.speak(tvWord.getText().toString(), QUEUE_ADD, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @OnClick({R.id.tvTapToReview, R.id.tvIKnew, R.id.tvNotKnown, R.id.iv_sound1, R.id.iv_sound2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTapToReview:
                layoutQuestion.setVisibility(View.GONE);
                layoutAnswer.setVisibility(View.VISIBLE);
                break;
            case R.id.tvIKnew:
            case R.id.tvNotKnown:
                boolean isLevelUp = false;
                if (view.getId() == R.id.tvIKnew) {
                    isLevelUp = true;
                }
                DatabaseManager.getInstance(this).updateWordLevel(currentWord, isLevelUp);
                if (wordsReviewedCounter <= wordReviewCount) {
                    layoutQuestion.setVisibility(View.VISIBLE);
                    layoutAnswer.setVisibility(View.GONE);
                    loadData();
                } else {
                    finish();
                }
                break;
            case R.id.iv_sound1:
            case R.id.iv_sound2:
                //play audio
                textToSpeech.speak(tvWord.getText().toString(), QUEUE_ADD, null);
                break;
        }
    }
    private boolean canSpeak = true;
    @Override
    public void onSuccess(TextToSpeech tts) {
        this.textToSpeech = tts;
        canSpeak = true;
    }

    @Override
    public void onFailure(TextToSpeech tts) {
        canSpeak = false;
        finish();
    }
}
