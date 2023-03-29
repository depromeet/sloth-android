# 나나공 (나보다 나무늘보가 공부 열심히 한다)

<img width="1228" alt="image" src="https://user-images.githubusercontent.com/51016231/225545924-9dd06f2a-bc89-45ac-aea4-3182bda66b95.png">

디프만 10기 4조 신동빈센조 나나공 Android Repository

- 구글 플레이스토어: https://play.google.com/store/apps/details?id=com.depromeet.sloth
- Disquiet: https://disquiet.io/product/나나공
- Behance: https://www.behance.net/gallery/133424149/_?locale=ko_KR
- figma: [Android Design](https://www.figma.com/file/e4rZW5rErfa7LCkettW918/%5B%EB%94%94%ED%94%84%EB%A7%8C-10%EA%B8%B0%5D-%EB%82%98%EB%82%98%EA%B3%B5?node-id=496-1662&t=TmGwHP2mcORNkPdG-0)

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
  - 수강할 강의의 필수 정보들을 입력하여 강의를 등록합니다.(나공이와 대결을 시작합니다!)
  
- 강의 조회(오늘까지 들어야하는 강의, 전체 강의)
  - 오늘까지 몇개의 강의를 들어야 하는지 확인하고, 강의가 몇일 남았는지, 현재 진행률를 확인합니다.
  
- 강의 수강 체크
  - 강의를 수강한 만큼 + 버튼을 눌러 체크합니다.
  
- 강의 수강 종료(완강)
  - 모든 강의를 수강한 경우 강의 완강하기 버튼을 눌러 완강합니다.
  
- 강의 상세 확인
  - 강의의 상세 정보를 확인합니다. 현재 진행률과 더불어 낭비하고 있는 돈이 얼마인지 확인할 수 있습니다.
  
- 강의 수정 
  - 등록한 강의의 정보를 수정합니다.
  
- 강의 삭제 
  - 등록한 강의를 삭제합니다. (도전을 포기합니다.)
  
- 프로필 수정
  - 사용자의 닉네임을 수정합니다. 
  
- 푸시 알림 수신
  - 푸시 알림을 통해 등록한 강의에 대한 정보들을 수신합니다.
  
- 알림 목록 확인

- 수강한 강의 통계 확인
  - 현재까지 등록한 전체 강의와 완강한 강의를 기반한 통계를 제공합니다

- 온보딩 튜토리얼 
  - 앱을 사용하기 전에 튜토리얼을 통해 앱의 사용방법을 익힐 수 있습니다.
  

**Technology Stack**
- Tools : Android Studio
- Language : Kotlin
- Architecture Pattern : [Google Recommend Architecture](https://developer.android.com/topic/architecture#recommended-app-arch)

<p align="start">
  <img src="https://developer.android.com/topic/libraries/architecture/images/mad-arch-overview.png" width="50%"/>
</p>

- Android Architecture Components(AAC)
  - ViewModel
  - DataBinding
  - Naivgation Conponent
  - Hilt
  - DataStore
- Coroutine
- Flow
- Retrofit2
- Okhttp3
- Gson
- KAKAO_SDK
- Glide
- Firebase 
  - Analytics
  - Cloud Messaging
  - Crashlytics 
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
- [ ] 네트워크 상태 처리 개선 (앱 안정화)
- [ ] 회원 탈퇴 기능 구현
- [ ] 프로필 사진 변경 기능 구현
- [ ] Manage 화면 내에 달력 구현
- [ ] 알림 목록 화면 내에 온보딩 다시 해보기 기능 구현

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


