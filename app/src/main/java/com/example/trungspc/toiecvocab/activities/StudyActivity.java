package com.example.trungspc.toiecvocab.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;
import com.example.trungspc.toiecvocab.databases.models.WordModel;
import com.example.trungspc.toiecvocab.utils.CommonConst;
import com.example.trungspc.toiecvocab.utils.HelperClass;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "StudyActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_topic_name)
    TextView tvTopicName;
    @BindView(R.id.tv_origin)
    TextView tvOrigin;
    @BindView(R.id.tv_pronounce)
    TextView tvPronounce;
    @BindView(R.id.tv_details)
    TextView tvDetails;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.iv_word)
    ImageView ivWord;
    @BindView(R.id.tv_example_en)
    TextView tvExampleEn;
    @BindView(R.id.tv_example_vn)
    TextView tvExampleVn;
    @BindView(R.id.tv_didnt_know)
    TextView tvDidntKnow;
    @BindView(R.id.tv_knew)
    TextView tvKnew;
    @BindView(R.id.cl_detail_part)
    ConstraintLayout clDetailPart;
    @BindView(R.id.cv_word)
    CardView cvWord;
    @BindView(R.id.rl_background)
    RelativeLayout rlBackground;
    @BindView(R.id.tv_explain)
    TextView tvExplaination;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.cl_full)
    ConstraintLayout clFull;
    @BindView(R.id.guideline)
    Guideline guideline;
    @BindView(R.id.iv_sound)
    ImageView ivSound;

    WordModel wordModel;
    int preID = -1;
    Animator animatorSet;
    boolean canSpeak = true;
    SharedPreferences sharedPreferences;

    TextToSpeech mTTS = null;
    private final int ACT_CHECK_TTS_DATA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        ButterKnife.bind(this);

        loadData();
        setupUI();
        checkTtsData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                mTTS = new TextToSpeech(this, this);
            } else {
                // Data is missing, so we start the TTS
                // installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    // Dispose the Text to Speech engine if close the app or exit the activity
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    private void setupUI() {
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    private void loadData() {
        TopicModel topicModel = (TopicModel) getIntent().getSerializableExtra("topic");

        tvTopicName.setText(topicModel.getName());
        rlBackground.setBackgroundColor(Color.parseColor(topicModel.getColor()));

        wordModel = DatabaseManager.getInstance(this).getRandomWord(topicModel.getId(), preID);
        preID = wordModel.getId();

        tvOrigin.setText(wordModel.getOrigin());
        tvPronounce.setText(wordModel.getPronunciation());
        tvExampleEn.setText(wordModel.getExample());
        tvExampleVn.setText(wordModel.getExampleTranslation());
        tvType.setText(wordModel.getType());
        tvExplaination.setText(wordModel.getExplanation());
        Picasso.get().load(wordModel.getImageUrl()).into(ivWord);
        String level = HelperClass.getLevel(wordModel.getLevel());
        tvLevel.setText(level);
        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        if(canSpeak && sharedPreferences.getBoolean(CommonConst.PLAY_SOUND_AUTO, false)) {
            if (mTTS != null) {
                speak(wordModel.getOrigin().trim(), 1);
            }
        }
    }

    // Check to see if we have TTS voice data
    private void checkTtsData() {
        Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(ttsIntent, ACT_CHECK_TTS_DATA);
    }

    private void speak(String text, int qmode) {
        if (qmode == 1) {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_details, R.id.tv_didnt_know, R.id.tv_knew, R.id.iv_sound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_details:
                clFull.setLayoutTransition(new LayoutTransition());
                changeContent(false);
                break;
            case R.id.tv_didnt_know:
                nextWord(false);
                break;
            case R.id.tv_knew:
                nextWord(true);
                break;
            case R.id.iv_sound:
                speak(wordModel.getOrigin().trim(), 1);
                break;
        }
    }

    public void changeContent(boolean isExpanded) {
        if (isExpanded) {
            clDetailPart.setVisibility(View.GONE);
            tvDetails.setVisibility(View.VISIBLE);
        } else {
            clDetailPart.setVisibility(View.VISIBLE);
            tvDetails.setVisibility(View.GONE);
        }
    }

    public void nextWord(final boolean isKnown) {
        setAnimation(R.animator.animation_move_to_left);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                DatabaseManager.getInstance(StudyActivity.this).updateWordLevel(wordModel, isKnown);
                loadData();

                clFull.setLayoutTransition(null);
                changeContent(true);
                setAnimation(R.animator.animation_move_from_right);
            }
        });
    }

    public void setAnimation(int animation) {
        animatorSet = AnimatorInflater.loadAnimator(this, animation);
        animatorSet.setTarget(cvWord);
        animatorSet.start();
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            canSpeak = true;
            if (mTTS != null) {
                int result = mTTS.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "onInit: TTS is ready");
                }
            }
        } else {
            canSpeak = false;
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_LONG).show();
        }
    }
}
