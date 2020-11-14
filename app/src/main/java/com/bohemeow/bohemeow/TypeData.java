package com.bohemeow.bohemeow;

public class TypeData {

    int image;
    int primage;
    String name;
    String detail;

    public TypeData(int image, int primage, String name, String detail) {
        this.image = image;
        this.primage = primage;
        this.name = name;
        this.detail = detail;
    }

    public static TypeData[] makeTypeData(){
        TypeData[] TypeDatas = new TypeData[9];
        TypeDatas[0] = new TypeData(R.drawable.cathead_null, R.drawable.parametercircle_null, "이름", "고양이 설명");
        TypeDatas[1] = new TypeData(R.drawable.hanggangic, R.drawable.hangangpr, "한강", "한강공원에 사는 한강이는 소풍 온 사람들의 도시락 냄새를 좋아합니다. 푸른 하늘 파란 강을 가로지르는 한강이는 부드러운 배털과 친근한 성격으로 산책하는 사람들의 이목을 끌곤 합니다");
        TypeDatas[2] = new TypeData(R.drawable.bameeic, R.drawable.bameepr, "밤이", "다정하고 사람을 좋아하는 밤이는 나뭇잎 사각이는 소리를 사랑합니다. 때로 대담하게 떠나는 것을 즐기는 밤이는 여수바닷가 이순신광장의 스타입니다. 언젠가 케이블카를 타보고 싶습니다.");
        TypeDatas[3] = new TypeData(R.drawable.chachaic, R.drawable.chachapr, "Chacha", "이태원에 사는 chacha는 루프탑에서 남산 바라보기를 좋아합니다. 맑은 하늘을 배경으로 펼쳐진 남산타워의 운치를 즐기기 때문에요. 시끌시끌한 다국적 수다 엿듣기를 즐기는 chacha는 이태원 붐비는 골목에서 자주 발견됩니다.");
        TypeDatas[4] = new TypeData(R.drawable.ryoniic, R.drawable.ryonipr, "려니", "제주 사려니숲길에 사는 려니는 햇빛 속에 흔들리는 잎사귀를 좋아합니다. 시끄러운 걸 좋아하지는 않지만 고요한 숲속에서 간간이 들리는 여행자들의 웃음에 귀 기울이기도 합니다.");
        TypeDatas[5] = new TypeData(R.drawable.moonmoonic, R.drawable.moonmoonpr, "문문", "해운대 달맞이길에 사는 문문이는 보름달과 산책하러 오는 사람들을 좋아합니다. 밤이면 사람들 구경을 하다가 때로 바다 냄새를 맡으며 부산을 가로지르는 차가운 도시의 고양이입니다.");
        TypeDatas[6] = new TypeData(R.drawable.popoic, R.drawable.popopr, "포포", "강릉 경포호에 사는 포포는 늦저녁 풀벌레 소리를 들으며 홀로 호숫가를 산책합니다. 소나무 사각이는 소리를 들으며 최고의 솔방울을 찾아 멀리 떠나는 상상을 자주 합니다.");
        TypeDatas[7] = new TypeData(R.drawable.taetaeic, R.drawable.taetaepr, "태태", "울산대교에 사는 태태는 한낮 태화강변을 따라 산책하기를 좋아합니다. 언제든 홀로 훌쩍 떠나는 것을 선호해 늘 혼자서 다리를 건너는 모습이 눈에 띕니다.");
        TypeDatas[8] = new TypeData(R.drawable.sessakic, R.drawable.sessackpr, "새싹이", "전주 한옥마을에 사는 새싹이는 새싹비빔밥 냄새를 좋아합니다. 독립적인 성격의 새싹이는 해가 지면 밥짓는 냄새를 맡으며 홀로 골목골목 다니는 차가운 도시 고양이입니다.때로 대담하게 기왓장 위를 걷기도 합니다.");

        return TypeDatas;
    }

    public int getImage() {
        return image;
    }

    public int getPrimage() {
        return primage;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }
}
