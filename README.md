# 원티드 프리온보딩 안드로이드

* [1. Project Introduction](#1-project-introduction)
* [2. People](#2-people)
* [3. Architecture](#3-architecture)
* [4. Feature & Screen](#4-feature---screen)
    + [1. 대시보드](#1-대시보드)
    + [2. 측정 하기](#2-측정-하기)
    + [3. 측정 그래프 띄우기](#3-측정-그래프-띄우기)
    + [4. 재생 하기](#4-재생-하기)
    + [5. Unit Test](#5-unit-test)
* [5. Technology Stack](#5-technology-stack)
* [6. Convention](#6-convention)
* [7. How to run](#7-how-to-run)

## 1. Project Introduction

[2주차 과제 링크](https://www.notion.so/8a916a4656b742dc83c586ccc93751e0)

<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white"> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white">

> 원티드 프리온보딩 2차 기업 과제
>

> 6축 데이터(가속도(acc) 3축 + 각속도(gyro) 3축)를 측정하는 앱 서비스
>

> 6축 데이터를 수집하여 로컬 저장소에 저장
>

> 저장된 데이터를 불러와서 그래프로 표현
>

## 2. People

| 권혁준 | 이서윤 | 이재성 | 이현섭 | 임수진 |
| ------------ | ------------ | ------------ | ------------ | ------------ |
| 프로젝트 셋팅, 테스트 | 재생 및 정지 기능 | 대시보드 띄우기 | 그래프 그려주기 | 측정 및 저장 기능 |

---

## 3. Architecture

> Clean Architecture + MVVM Pattern

<img width="400" alt="image" src="https://user-images.githubusercontent.com/85485290/193095723-50969ba7-19f1-46d7-8c91-c76cc3747f8b.png">

```
🔖
.
├── common
├── data
│   ├── converter
│   ├── dao
│   ├── database
│   ├── entity
│   ├── paging
│   └── repository
├── di
├── domain
│   ├── mapper
│   ├── model
│   ├── repository
│   └── usecase
└── presentation
    ├── common
    │   ├── adapter
    │   ├── base
    │   ├── state
    │   └── util
    ├── dashboard
    ├── measurement
    └── replay

```
---

## 4. Feature & Screen

### 1. 대시보드
* Room Local DB에 저장되어 있는 측정 데이터를 가져와 무한 스크롤되도록 구현 (Paging)
* 항목 클릭 및 다이얼로그 클릭에 따른 화면 전환 구현(Navigation)

#### Dao
```kotlin
@Dao
interface MeasurementDAO {
    @Query("SELECT * from measurements ORDER BY date DESC LIMIT :loadSize OFFSET (:page - 1) * :loadSize")
    suspend fun getAllMeasurement(page: Int, loadSize: Int): List<MeasurementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeasurement(measurementEntity: MeasurementEntity)

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun deleteMeasurementById(id: Int)
}
```
* Room DB 질의를 위한 인터페이스
* 페이징 시 `loadSize`를 통한 `LIMIT`과 그에 따른 `OFFSET`을 통해 데이터를 가져옴
    * ex) page 1 - OFFSET 0 // page 2 - OFFSET 10
* PK인 id를 이용한 삭제


#### PagingSource
```kotlin
class MeasurementPagingSource(
    private val dao: MeasurementDAO
) : PagingSource<Int, MeasureResult>() {
    override fun getRefreshKey(state: PagingState<Int, MeasureResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MeasureResult> {
        val page = params.key ?: STARTING_KEY

        if (page != STARTING_KEY) delay(PAGING_DELAY)

        return try {
            val measureResult = dao.getAllMeasurement(page, params.loadSize).mapToMeasureResult()

            LoadResult.Page(
                data = measureResult,
                prevKey = if (page == STARTING_KEY) null else page - 1,
                nextKey = if (measureResult.isEmpty()) null else page + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}
```
* `MeasurementPagingSource#getRefreshKey`를 통해 데이터 로드시 사용될 Key 값을 가져옴
* `MeasurementPagingSource#load`를 통해 dao에 접근하여 prevKey와 nextKey에 맞게 데이터를 반환

#### RepositoryImpl#getAllMeasurement & UseCase
```kotlin
override suspend fun getAllMeasurement(): Flow<PagingData<MeasureResult>> {
    return Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
            initialLoadSize = PAGE_SIZE
        ),
        pagingSourceFactory = { MeasurementPagingSource(measurementDao) }
    ).flow
}
```
```kotlin
class GetAllMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke() =
        measurementRepository.getAllMeasurement().flowOn(Dispatchers.Default)

}
```

#### ViewModel
```kotlin
private val _measureData: MutableStateFlow<PagingData<MeasureResult>> =
        MutableStateFlow(PagingData.empty())
val measureData: StateFlow<PagingData<MeasureResult>> = _measureData.asStateFlow()


fun getAllMeasurement() {
    viewModelScope.launch {
        getAllMeasurementUseCase.invoke()
            .cachedIn(viewModelScope)
            .collectLatest { measureList ->
                _measureData.emit(measureList)
            }
    }
}
```
* 가져온 측정 데이터를 ViewModelScope상에 캐시
* StateFlow를 통해 emit

#### Fragment#observeMeasureData
```kotlin
private fun observeMeasureData() {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.measureData.collectLatest { measureList ->
                pagingAdapter.submitData(measureList)
            }
        }
    }
}
```
* Lifecycle의 State가 `STARTED` 상태로 도달 할때만 데이터를 수집하여 adapter에 전달한다.
* `launchWhenXXX`와 같은 flow 확장함수와는 달리 `repeatOnLifecycle`을 사용하면 해당 State 도달 시 자동으로 수집 중단 및 업스트림 flow를 취소한다.

#### 문제 해결
1. Paging 시 동일 데이터를 반복적으로 가져오던 이슈
   https://github.com/DavidKwon7/android-wanted-SensorDashboardApp/pull/14

2. 데이터 삭제 후 UI가 리프레시 되지 않는 이슈

```kotlin
private fun showDialog(measureResult: MeasureResult) {
    val dialog = OptionDialog(
        requireContext(),
        playClicked = {
            navigateWithArgs(
                DashboardFragmentDirections.actionDashboardToReplay(
                    measureResult,
                    ViewType.PLAY
                )
            )
        },
        deleteClicked = {
            viewModel.deleteMeasurementById(measureResult.id)
            updateMeasureResult()
        })

    dialog.showDialog()
}

private fun updateMeasureResult() {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            kotlin.runCatching {
                viewModel.getAllMeasurement()
            }.onSuccess {
                viewModel.measureData.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }
    }
}
```
* 다이얼로그가 dismiss되면서 `RESUME`상태가 될 때 `getAllMeasurement()`를 호출하여 데이터를 갱신하는 형태로 구현

#### 결과

<img src="https://user-images.githubusercontent.com/51078673/193296404-250ada2d-83b8-42b1-b7b9-bc59959e9096.gif" width=300>



---

### 2. 측정 하기

| case | acc 측정 | gyro 60초 까지 측정 | acc 측정 중 gyro로 전환하여 측정 | 빈 측정 값 존재 |
| --- | --- | --- | --- | --- |
| 화면 | ![ezgif com-gif-maker](https://user-images.githubusercontent.com/85485290/193100784-b4b6cc3a-1948-4e2b-a06b-e5d56c9aed2c.gif) | ![ezgif com-gif-maker (1)](https://user-images.githubusercontent.com/85485290/193100811-dc012c7d-ed4b-4efd-be60-ff92623cc48b.gif) | ![ezgif com-gif-maker (2)](https://user-images.githubusercontent.com/85485290/193100800-93ebc9f0-e752-4551-ac36-92ff1fb9159f.gif) | <img width="250" src="https://user-images.githubusercontent.com/85485290/193100842-6aad57b0-da21-4d2d-8c1f-b476ab9f89d9.jpg" /> |


#### 측정 값을 `Room`에 저장하는 방식
- `MeasurementEntity`

```kotlin
@Entity(tableName = "measurements")
@JsonClass(generateAdapter = true)
data class MeasurementEntity(
    val sensorList: List<SensorInfo>? = null,
    val type: String,
    val date: String,
    val time: Double = 0.0,
) {
    @PrimaryKey(autoGenerate = true) var id: Int =0
}

@JsonClass(generateAdapter = true)
data class SensorInfo(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) {
    companion object {
        fun emptyInfo() = SensorInfo(0, 0, 0)
    }
}
```

- `SensorListTypeConverter`
- json converter로 `moshi` 사용

```kotlin
private val listType = Types.newParameterizedType(List::class.java, SensorInfo::class.java)
private val adapter: JsonAdapter<List<SensorInfo>> = moshi.adapter(listType)

    // string -> list로 DB에서 가져오기
    @TypeConverter
    fun fromString(value: String): List<SensorInfo>? {
        return if(value.isEmpty()) {
            listOf()
        } else {
            adapter.fromJson(value)
        }
    }

    // list -> string으로 DB에 보내기
    @TypeConverter
    fun fromSensorList(type: List<SensorInfo>): String {
        return adapter.toJson(type)
    }
```

#### 측정 값을 10Hz 주기로 읽어 들이기
1. 처음 선택한 방법
    - coroutine 내부에서 `delay`를 걸어서 센서 값을 주고 받는 방법
    - coroutine의 `channel`로 구현 가능

```kotlin
private val channel = Channel<SensorInfo>()

lifecycleScope.launch {
            // 0.1초마다 send
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                delay(PERIOD)
                channel.send(sensorInfo)

                viewModel.curSecond.collect {
                    if (it >= MAX) {
                        // 60초 지나면 측정 중지
                        stopMeasurement()
                        this@launch.cancel()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                for (sensor in channel) {
                  // 0.1초마다 receive
                  viewModel.updateMeasurement()
                }
            }
        }
```

2. 변경 후 방법
    - sensor를 등록 할 때 `samplingPeriodUs` 값을 원하는 주기에 맞게 변경

```kotlin
when (viewModel.curMeasureTarget.value) {
                MeasureTarget.ACC -> {
                    sensorManager.registerListener(
                        this,
                        accSensor,
                        SENSOR_DELAY_CUSTOM
                    )
                }

                MeasureTarget.GYRO -> {
                    sensorManager.registerListener(
                        this,
                        gyroSensor,
                        SENSOR_DELAY_CUSTOM
                    )
                }
            }
```

- 값 읽어 들이기

```kotlin
override fun onSensorChanged(sensorEvent: SensorEvent?) {

        val sensorInfo = when (sensorEvent?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE  -> {
                SensorInfo(
                    x = sensorEvent.values[0].toInt(),
                    y = sensorEvent.values[1].toInt(),
                    z = sensorEvent.values[2].toInt(),
                )
            }

            else -> {
                SensorInfo.emptyInfo()
            }
        }

				/**
					센서 값 & 차트 & 시간(second) 업데이트
				**/
}
```

- 읽어 들인 값 저장

```kotlin

viewModelScope.launch(dispatcher) {
            kotlin.runCatching {
                saveMeasurementUseCase(
                    sensorList = _sensorList.value,
                    type = _curMeasureTarget.value.type,
                    date = date,
                    time = curSecond.value
                )
            }
                .onSuccess {
                    Timber.tag(TAG).e("저장 성공")
                    _uiState.value = MeasurementUiState.SaveSuccess
                    clearMeasurementInfo()
                }
                .onFailure {
                    Timber.tag(TAG).e(it)
                    _uiState.value = MeasurementUiState.SaveFail
                }
        }

```

#### `Accelerometer` 와 `Gyroscope` 화면 분기

```kotlin
enum class MeasureTarget(val type: String) {
    ACC("Accelerometer"),
    GYRO("Gyro")
}
```

---

### 3. 측정 그래프 띄우기

차트 만들기

```kotlin
	val entriesX = ArrayList<Entry>()
        val entriesY = ArrayList<Entry>()
        val entriesZ = ArrayList<Entry>()

        var i = 1F

        for (it in sensorInfoList) {
            entriesX.add(Entry(i, it.x.toFloat()))
            entriesY.add(Entry(i, it.y.toFloat()))
            entriesZ.add(Entry(i, it.z.toFloat()))
            i++
        }

        val dataSetX = LineDataSet(entriesX, "X")
        val dataSetY = LineDataSet(entriesY, "Y")
        val dataSetZ = LineDataSet(entriesZ, "Z")

        dataSetX.color = Color.RED
        dataSetX.setDrawCircles(false)
        dataSetX.setDrawValues(false)

        dataSetY.color = Color.GREEN
        dataSetY.setDrawValues(false)
        dataSetY.setDrawCircles(false)

        dataSetZ.color = Color.BLUE
        dataSetZ.setDrawValues(false)
        dataSetZ.setDrawCircles(false)

        val lineData = LineData()

        lineData.addDataSet(dataSetX)
        lineData.addDataSet(dataSetY)
        lineData.addDataSet(dataSetZ)

        binding.measurementLineChart.apply {
            data = lineData
            lineData.notifyDataChanged()
            notifyDataSetChanged()
            invalidate()
        }

```

값이 업데이트 될때마다 차트 데이터 넣어주기

```kotlin
sensorInfoList.add(sensorInfo)
        updateChart()
```

아쉬운점 데이터가 들어올 때마다 차트 데이터를 다시만든다.

부분 해결 : 차트 만들기

```kotlin
private fun initChart(){
        val lineData = LineData()
        binding.measurementLineChart.data=lineData
    }
```

값이 들어올때마다 변동한 값을 반영한다.

```kotlin
private fun addEntry(sensorInfo: SensorInfo) {
        val data: LineData = binding.measurementLineChart.data

        var setX = data.getDataSetByIndex(0) // 0번째 위치의 데이터셋을 가져옴

        if (setX == null) // 0번에 위치한 값이 없으면
        {
            setX = "X".createSet(Color.RED)
            data.addDataSet(setX) // createSet을 한 set을 데이터셋에 추가함
            
        }

        data.addEntry(Entry(setX.entryCount.toFloat(), sensorInfo.x.toFloat()), 0)

        data.notifyDataChanged() // data의 값 변동을 감지함
        binding.measurementLineChart.notifyDataSetChanged() // chart의 값 변동을 감지함
        binding.measurementLineChart.invalidate()
    }
```

```kotlin
private fun String.createSet(chartColor: Int): ILineDataSet {
        val set = LineDataSet(null, this)
        set.color = chartColor
        set.setDrawCircles(false)
        set.setDrawValues(false)
        return set
    }
```

이 방법을 채택 못한 이유 → line 한개는 잘 실행되지만  dataset을 여러개 만들려고 하니 값들이 한번에 합쳐진다.

---

### 4. 재생 하기

#### 1. 주어진 시간 만큼 타이머 작동

| timer |
|:----:|
|<img src="https://user-images.githubusercontent.com/110798031/193276742-9a7e9524-d26d-48b9-94bd-0665fa885e64.gif" width="180" height="400">|
- `ReplayViewModel`
- 정지했을 경우 0초부터 다시 시작
- ui state에 따라 버튼 모양 변경 및 타이머 시작/정지

```kotlin
@HiltViewModel
class ReplayViewModel @Inject constructor (
): ViewModel() {

    var measureTime = 0

    private lateinit var timerJob : Job

    private fun startTimer() {
        if(::timerJob.isInitialized) {
            timerJob.cancel()
        }

        _timerCount.value = 0
        timerJob = viewModelScope.launch {
            while (timerCount.value < measureTime) {
                _timerCount.value = timerCount.value + 1
                delay(100L)
            }

            changeTimerStatus()
            _timerCount.value = measureTime
        }
    }

    private fun stopTimer() {
        if (::timerJob.isInitialized) {
            timerJob.cancel()
        }
    }

    fun changeTimerStatus() {
        when (curPlayType.value) {
            PlayType.Stop -> {
                _curPlayType.value = PlayType.Play
                startTimer()
            }
            PlayType.Play -> {
                _curPlayType.value = PlayType.Stop
                stopTimer()
            }
        }
    }

    fun applyTimeFormat(time: Double) {
        measureTime = (time * 10).toInt()
    }
}
```

#### 2. ViewType에 따른 ui 상태 관리

| view type | play type |
|:----:|:----:|
|<img src="https://user-images.githubusercontent.com/110798031/193276497-567f090d-b542-4e34-89db-1aae6f61ecd6.gif" width="180" height="400">|<img src="https://user-images.githubusercontent.com/110798031/193276295-d6c7a60c-c5d8-4a13-b96a-21c26b38c749.gif" width="180" height="400">|
- `ReplayBindingAdapter`
- xml에 바인딩하여 보여지는 컴포넌트 분기 처리

```kotlin
@BindingAdapter("stopVisibilityPlayType", "stopVisibilityViewType")
fun changeStopVisibility(view: ImageView, playType: PlayType?, viewType: ViewType?) {
    if (viewType == null || playType == null) return
    when (viewType) {
        ViewType.PLAY -> {
            when (playType) {
                is PlayType.Stop -> {
                    view.visibility = View.VISIBLE
                }
                is PlayType.Play -> {
                    view.visibility = View.GONE
                }
            }
        }
        ViewType.VIEW -> {
            view.visibility = View.GONE
        }
        ViewType.INITIAL -> {
            // Error Status, when initial status, users cannot enter replay fragment
        }
    }
}

@BindingAdapter("playVisibilityPlayType", "playVisibilityViewType")
fun changePlayVisibility(view: ImageView, playType: PlayType?, viewType: ViewType?) {
    if (viewType == null || playType == null) return

    when (viewType) {
        ViewType.PLAY -> {
            when (playType) {
                is PlayType.Stop -> {
                    view.visibility = View.GONE
                }
                is PlayType.Play -> {
                    view.visibility = View.VISIBLE
                }
            }
        }
        ViewType.VIEW -> {
            view.visibility = View.GONE
        }
        ViewType.INITIAL -> {
            // Error Status, when initial status, users cannot enter replay fragment
        }
    }
}

@BindingAdapter("timerVisibilityPlayType", "timerVisibilityViewType")
fun changeTimerVisibility(view: TextView, playType: PlayType?, viewType: ViewType?) {
    if (viewType == null || playType == null) return

    when (viewType) {
        ViewType.PLAY -> {
            view.visibility = View.VISIBLE
        }
        ViewType.VIEW -> {
            view.visibility = View.GONE
        }
        ViewType.INITIAL -> {
            // Error Status, when initial status, users cannot enter replay fragment
        }
    }
}
```

---

### 5. Unit Test

1. `MainCoroutineRule` 생성

```kotlin
@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher){

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
```

2. 테스트 환경

```kotlin
@get:Rule
val mainCoroutineRule = MainCoroutineRule()

@MockK
private lateinit var measurementRepository: MeasurementRepository

private lateinit var measurementViewModel: MeasurementViewModel

@Before
fun setUp() {
    MockKAnnotations.init(this, relaxUnitFun = true)
		measurementViewModel = MeasurementViewModel(
        measurementRepository = measurementRepository,
        dispatcher = mainCoroutineRule.dispatcher
    )
}
```

3. 테스트

```kotlin
@Test
fun test() {
        runBlocking {
            flow {
                emit("test")
                emit("test")
            }.test {
                assertThat(expectItem()).isEqualTo("test")
                assertThat(expectItem()).isEqualTo("test")
                expectComplete()
            }
        }
    }
```

```kotlin
@Test
fun paging_source_load_failure_received_io_exception() =
    mainCoroutineRule.runBlockingTest{
        val error = IOException("404", Throwable())

        coEvery{measurementDAO.getAllMeasurement()} throws error

        val expectedResult = PagingSource.LoadResult.Error<Int, ClipData.Item>(error)

        Assert.assertEquals(
            expectedResult, measurementPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }
```

---

## ****5. Technology Stack****

- Tools : Android Studio Dolphin
- Language : Kotlin
- Architecture Pattern : MVVM Pattern
- Android Architecture Components(AAC)
    - Flow
    - ViewModel
    - Coroutine
    - Data Binding
- Navigation Component
- PAGING
- HILT
- ROOM
- MOSHI
- TIMBER
- MPAndroidChart
- Unit Test
    - JUnit4
    - MockK
    - Turbine

---

## ****6. Convention****

### **1. Git Convention**

[Git Convention](https://www.notion.so/a1dc40cca82c4e4ca3fb3f97f25cd562)

### 2. Commit Convention

[Commit Convention](https://www.notion.so/82e40ee38c0b4d249951cbf808b9394d)

### 3. Coding Convention

[Coding Convention](https://www.notion.so/1df208ab2a594dc0ad76633a7f84637c)

---

## 7. **How to run**

1. Clone this repository

```
git clone https://github.com/DavidKwon7/android-wanted-SensorDashboardApp.git
```

2. Type in your terminal

```
git checkout main
```

3. Run this project in Android Studio
