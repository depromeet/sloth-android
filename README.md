# 나나공 (나보다 나무늘보가 공부 열심히 한다)
![앱 아이콘](https://user-images.githubusercontent.com/51016231/200228430-b5de928b-fe20-4578-8165-721b54463ef1.png)

디프만 10기 4조 신동빈센조 나나공 Android Repository

- 구글 플레이스토어: https://play.google.com/store/apps/details?id=com.depromeet.sloth
- Disquiet: https://disquiet.io/product/나나공
- Behance: https://www.behance.net/gallery/133424149/_?locale=ko_KR

# Overview
> 모든 인강을 수강하는 사람들을 위한 강의 수강 독려 서비스

- 개발 기간 : 2021.09.11 ~ing
- Team 
  - Android Developer :  [이지훈](https://github.com/easyhooon)  [최철훈](https://github.com/ImIrondroid)  
- [Trouble Shooting](https://github.com/depromeet/sloth-android/wiki)
# About

**Features**
- 구글 로그인 
- 카카오 로그인 
- 강의 등록
- 강의 조회(오늘까지 들어야하는 강의, 전체 강의)
- 강의 수강 체크 
- 강의 상세 확인
- 강의 수정 
- 강의 삭제 
- 프로필 수정
- 푸시 알림 수신
- 알림 목록 확인
- 수강한 강의 통계 확인
- 온보딩 튜토리얼 

**Technology Stack**
- Tools : Android Studio
- Language : Kotlin
- Architecture Pattern : Google recommend architecture 
- Android Architecture Components(AAC)
  - ViewModel
  - DataBinding
  - Naivgation Conponent
  - Hilt
- Coroutine
- Flow
- Retrofit2
- Okhttp3
- Gson
- KAKAO_SDK
- Glide
- Firebase Analytics
- Firebase Cloud Messaging
- Firebase Crashlytics 
- Lottie
- Timber 
- [ProgressView](https://github.com/skydoves/ProgressView)
- StartUp

**Foldering**
```
.
├── app
│   ├── di
│   ├── initialize
│   └── application
├── buildSrc
├── data
│   ├── mapper
│   ├── model
│   ├── network
│   ├── paging
│   ├── preferences
│   ├── repository
│   └── util
├── domain
│   ├── entity
│   ├── repository
│   ├── usecase
│   └── util
├── gradle
│   └── libs.versions.toml
└── presentation
    ├── adapter
    ├── di
    ├── extenstions
    ├── mapper
    ├── extenstions
    ├── service
    ├── ui
    └── util


```

# ToDo
- [x] 멀티 모듈화
- [ ] 네트워크 상태 처리 개선  

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


