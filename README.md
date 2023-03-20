# 나나공 (나보다 나무늘보가 공부 열심히 한다)

<img width="1228" alt="image" src="https://user-images.githubusercontent.com/51016231/225545924-9dd06f2a-bc89-45ac-aea4-3182bda66b95.png">

디프만 10기 4조 신동빈센조 나나공 Android Repository

- 구글 플레이스토어: https://play.google.com/store/apps/details?id=com.depromeet.sloth
- Disquiet: https://disquiet.io/product/나나공
- Behance: https://www.behance.net/gallery/133424149/_?locale=ko_KR

# Overview
![앱 아이콘](https://user-images.githubusercontent.com/51016231/200228430-b5de928b-fe20-4578-8165-721b54463ef1.png)
> 모든 인강을 수강하는 사람들을 위한 강의 수강 독려 서비스

- 개발 기간 : 2021.09.11 ~ing
- Team 
  Android Developer :  [이지훈](https://github.com/easyhooon)  [최철훈](https://github.com/ImIrondroid)  
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
<img width="1012" alt="image" src="https://user-images.githubusercontent.com/51016231/225786605-0af55efe-f608-419b-bd3b-9a2efc853e64.png">

- 홈화면 (투데이, 강의목록, 마이페이지(강의 통계), 알림목록)
<img width="1025" alt="image" src="https://user-images.githubusercontent.com/51016231/225782095-85a43d1a-98f7-49eb-96f9-9eacfa147721.png">

- 강의 등록
<img width="757" alt="image" src="https://user-images.githubusercontent.com/51016231/225783992-98d1d4bb-b322-4927-adfb-db4efb7c7971.png">

- 강의 상세화면, 수정화면, 삭제화면
<img width="756" alt="image" src="https://user-images.githubusercontent.com/51016231/225783651-9fabe4ed-e6e0-4c44-a4fc-ac6816de3b70.png">

- 설정 화면, 프로필 수정,문의, 개인정보 처리 방침
<img width="1031" alt="image" src="https://user-images.githubusercontent.com/51016231/225783202-679d053e-bc7b-4d51-9da4-e9ed6d5e2f14.png">


