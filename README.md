# 나나공 (나보다 나무늘보가 공부 열심히 한다)
![앱 아이콘](https://user-images.githubusercontent.com/51016231/200228430-b5de928b-fe20-4578-8165-721b54463ef1.png)

디프만 10기 4조 나나공 Android Project

- 구글 플레이스토어: https://play.google.com/store/apps/details?id=com.depromeet.sloth

# OverView
> 모든 인강을 수강하는 사람들을 위한 강의 수강 독려 서비스

- 개발 기간 : 2021.09.11 ~ing
- Team 
  - Android 개발자 : [최철훈](https://github.com/ImIrondroid)  [이지훈](https://github.com/easyhooon)

# About

**Features**
- 0. 구글 로그인 
- 1. 카카오 로그인 
- 2. 강의 등록
- 3. 강의 조회(오늘까지 들어야하는 강의, 전체 강의)
- 4. 강의 수강 체크 
- 5. 강의 상세 보기
- 6. 강의 수정 
- 7. 강의 삭제 
- 8. 프로필 수정
- 9. 푸시 알림

**Technology Stack**
- Tools : Android Studio
- Language : Kotlin
- Architecture Pattern : MVVM Pattern
- Android Architecture Components(AAC)
  - ViewModel
  - DataBinding
- Naivgation Conponent
- Coroutine
- Flow
- Hilt
- Retrofit2
- Okhttp3
- Gson
- KAKAO_SDK
- Glide
- FIREBASE_BOM
- Lottie
- Timber 

**Foldering**
```
.
├── data
│   ├── model
│   ├── network
│   └── repository
├── di
├── extensions
├── service
├── ui
└── util

```

# ToDo
- 푸시 알림 각 소셜 로그인별 고유 식별 처리 
- 네트워크 상태 처리 개선  

# WireFrame
- 로그인 
<img width="812" alt="image" src="https://user-images.githubusercontent.com/51016231/200229589-5ea2dab3-07ea-4bf5-8757-7e1fb8ac212f.png">

- 강의 등록
<img width="712" alt="image" src="https://user-images.githubusercontent.com/51016231/200233062-30177f75-33c7-4245-b2b1-b16c52db8a50.png">

- 홈화면 (투데이, 강의목록, 마이페이지)
<img width="716" alt="image" src="https://user-images.githubusercontent.com/51016231/200232753-7826bf68-6b9e-47a7-ae76-8cd0c880d00a.png">

- 강의 상세화면, 수정화면, 삭제화면
<img width="950" alt="image" src="https://user-images.githubusercontent.com/51016231/200232331-f890ea1b-0849-46b9-a60c-801e7910f573.png">

- 마이페이지 화면 메뉴 (프로필 수정, 문의, 개인정보 처리 방침, 로그아웃)
<img width="946" alt="image" src="https://user-images.githubusercontent.com/51016231/200232788-fd1b8d22-6403-443a-aa1b-7228ee0078a6.png">



