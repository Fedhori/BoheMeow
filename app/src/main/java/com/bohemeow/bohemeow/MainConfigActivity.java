package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainConfigActivity extends Activity {

    ImageView catFace;

    TextView user_name;
    TextView user_level;
    TextView total_count;
    TextView total_dis;
    TextView total_time;
    TextView total_spot;
    TextView user_introduction;

    Button edit_btn;
    Button tutorial_btn;
    Button logout_btn;
    Button delacc_btn;
    Button bugreport_btn;
    Button FAQ_btn;

    Button walkdataBtn;
    Button typedataBtn;

    TextView type_name;
    TextView type_detail;
    ImageView type_parameter;
    ImageView prtext;
    ImageView box;

    TypeData[] TypeDatas = new TypeData[9];

    boolean isWalk = true;
    int typeNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config_popup);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");
        typeNum = userData.catType;

        catFace = findViewById(R.id.cat_img);
        user_name = findViewById(R.id.user_name);
        user_level = findViewById(R.id.user_level);
        total_count = findViewById(R.id.total_count);
        total_dis = findViewById(R.id.total_dis);
        total_time = findViewById(R.id.total_time);
        total_spot = findViewById(R.id.total_spot);
        user_introduction = findViewById(R.id.tv_introduction);
        type_name = findViewById(R.id.typename2);
        type_detail = findViewById(R.id.typedetail2);
        type_parameter = findViewById(R.id.imageView38);
        prtext = findViewById(R.id.imageView41);
        box = findViewById(R.id.imageView35);


        total_spot.setText(userData.totalSpotCount + " 번");
        user_introduction.setText(userData.introduction);

        //set cat image
        TypeDatas = TypeData.makeTypeData();
        catFace.setImageResource(TypeDatas[typeNum].getImage());

        //set detail data
        user_name.setText(userData.nickname + "의 정보");
        user_level.setText("Lv. " + Integer.toString(calculateLevel(userData.level)));
        total_count.setText(Integer.toString(userData.totalWalkCount) + " 번");

        float distance = (float) userData.totalWalkLength / 1000f;
        String strNumber = String.format("%.2f", distance);
        total_dis.setText(strNumber + " km");

        long totalTime = userData.realWalkTime; // ms
        long hour;
        long minute;
        long second;

        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;

        String timeText = "";
        if(hour >= 10){
            timeText += String.valueOf(hour);
        }
        else{
            timeText += "0" + hour;
        }
        timeText += ":";
        if(minute >= 10){
            timeText += String.valueOf(minute);
        }
        else{
            timeText += "0" + minute;
        }
        timeText += ":";
        if(second >= 10){
            timeText += String.valueOf(second);
        }
        else{
            timeText += "0" + second;
        }
        total_time.setText(timeText);

        walkdataBtn = findViewById(R.id.button4);
        walkdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isWalk) {
                    walkdataBtn.setBackgroundResource(R.drawable.main_config_tap_on);
                    typedataBtn.setBackgroundResource(R.drawable.main_config_tap_off);
                    type_name.setText("");
                    type_detail.setText("");
                    box.setVisibility(View.INVISIBLE);
                    type_parameter.setVisibility(View.INVISIBLE);
                    prtext.setVisibility(View.INVISIBLE);
                    isWalk = true;
                }
            }
        });

        typedataBtn = findViewById(R.id.button5);
        typedataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWalk) {
                    walkdataBtn.setBackgroundResource(R.drawable.main_config_tap_off);
                    typedataBtn.setBackgroundResource(R.drawable.main_config_tap_on);
                    String text = TypeDatas[typeNum].getName();
                    if(typeNum == 1 || typeNum == 5) text += "은?";
                    else text += "는?";
                    type_name.setText(text);
                    type_detail.setText(TypeDatas[typeNum].getDetail());
                    box.setVisibility(View.VISIBLE);
                    type_parameter.setVisibility(View.VISIBLE);
                    type_parameter.setImageResource(TypeDatas[typeNum].getPrimage());
                    prtext.setVisibility(View.VISIBLE);
                    isWalk = false;
                }
            }
        });

        edit_btn = findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigEditActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        tutorial_btn = findViewById(R.id.tutorial_btn);
        tutorial_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, TutorialActivity.class);
                intent.putExtra("userdata", userData);
                startActivity(intent);
                finish();
            }
        });

        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigLogoutActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        delacc_btn = findViewById(R.id.delacc_btn);
        delacc_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigDelActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        bugreport_btn = findViewById(R.id.bugreport_btn);
        bugreport_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, BugReportActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        final FAQDAta[] faqData = getFaqData();
        FAQ_btn = findViewById(R.id.FAQ_btn);
        FAQ_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, FAQActivity.class);
                intent.putExtra("faqData", faqData);
                startActivityForResult(intent, 1);
            }
        });

        double currentLevel = 0;

        if(userData.level >= 10000){
            currentLevel = ((double) ((userData.level - 10000) % 1500)) / 15d;
        }
        else{
            currentLevel = ((double) userData.level % 1000) / 10d;
        }
    }

    FAQDAta[] getFaqData(){
        return new FAQDAta[]{
                new FAQDAta("기타/ 특정 글자가 써지지 않아요.", "현 폰트에서 지원하지 않는 문자는 출력이 되지 않을 수 있습니다. "),
                new FAQDAta("기타/ 메인의 날씨가 맞지 않아요.", "날씨는 마지막 산책 장소를 기준으로 보여주기 때문에, 같은 위치가 아닐 경우 전 지역의 날씨를 보여줍니다."),
                new FAQDAta("기타/ 버튼을 잘 눌리지 않아요.", "데이터 속도가 느릴 경우 데이터를 읽어오는데 많은 시간이 걸리기에 버튼이 잘 눌리지 않는 것으로 보일 수도 있습니다."),
                new FAQDAta("메인/ 스팟방문보상은 무엇인가요?", "방문한 스팟의 수에 따라 새로운 장식품이 추가됩니다. 산책을 진행하고 귀여운 장식품을 수집해보세요!"),
                new FAQDAta("설정/체중은 왜 입력하나요?", "산책 결과에서 정확한 칼로리를 계산하기 위해 사용합니다."),
                new FAQDAta("설정/ 자기소개는 어떻게 변경하나요?", "설정, 정보수정, 자기소개 변경 버튼을 통해 자기소개를 변경할 수 있습니다."),
                new FAQDAta("대회/ 포인트는 어떻게 쌓을 수 있나요?", "대회기간 동안은 경험치와 1:1 비율로 쌓이게 됩니다."),
                new FAQDAta("대회/ 사전예약 보상은 어떻게 확인하나요?", "사전예약자는 계정 생성 시 1회에 한하여 자동으로 1000포인트를 제공합니다."),
                new FAQDAta("대회/ 포인트는 언제까지 사용할 수 있나요?", "포인트는 11월 29일까지 사용 가능합니다."),
                new FAQDAta("대회/ 포인트를 사용하면 랭킹 점수도 내려가나요?", "아닙니다. 포인트와 경험치는 별개이며, 포인트를 사용하셔도 랭킹 측정에 사용되는 점수는 내려가지 않습니다."),
                new FAQDAta("대회/ 포인트가 쌓이지 않아요.", "포인트는 22일까지만 적립이 가능하며, 29일까지 사용이 가능합니다."),
                new FAQDAta("산책/ 추천 경로는 어떤 기능인가요?", "추천 산책은 이용자의 위치와 선호도를 기반으로, 주변의 산책 스팟을 추천해주는 기능입니다. 모험을 떠나고 싶을때 언제든 이용해보세요!"),
                new FAQDAta("산책/ 직접 선택 기능은 무엇인가요?", "스팟 선택 기능은 이용자가 원하는 위치를 목표 스팟으로 지정하여 경로를 추천받고, 산책을 즐길 수 있는 기능입니다."),
                new FAQDAta("산책/ 이전에 방문했던 경로가 사라졌어요.", "이전 경로는 최대 3개까지 저장되며, 더 많은 산책을 진행할 시 가장 오래된 경로부터 삭제됩니다."),
                new FAQDAta("산책/산책을 마쳐도 경험치가 쌓이지 않아요.", "산책도중 애플리케이션을 강제종료하셨거나, 너무 짧은 거리밖에 걷지 않으셨거나, 혹은 일반적인 보행과 다른 움직임이 감지된 경우 점수가 쌓이지 않습니다."),
                new FAQDAta("커뮤니티/게시글이 안올라가요.", "도배 방지를 위하여 하루에 연속해서 3개 이상의 게시글을 올릴 수 없습니다. 다른유저가 작성할 글을 기대해 보세요!"),
                new FAQDAta("커뮤니티/ 게시글을 작성했는데 경험치가 오르지 않아요.", "게시글 작성으로 얻을 수 있는 경험치는 하루 최대 3번의 제한이 있습니다."),
                new FAQDAta("커뮤니티/ 게시글에섭 보이는 레벨이 현재 제 레벨과 달라요.", "게시글에서 보이는 레벨은 해당 게시글을 작성될 때를 기준으로 합니다. 더 성장한 캐릭터를 자랑하고 싶으시다면 새로운 게시글을 작성해 주세요!")
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){

            Intent intent = new Intent();

            switch(resultCode){
                case RESULT_OK: // result_ok -> nothing happened
                    break;
                case 0: // background case
                    setResult(0, intent);
                    finish();
                    break;
                case 1: // logout & delete account case
                    setResult(1, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    int calculateLevel(int score){
        int level;
        if(score >= 10000){
            score -= 10000;
            level = (score / 1500) + 11;
        }
        else{
            level = score/1000 + 1;
        }
        return level;
    }
}
