<h3>스프링 프레임워크 핵심 기술</h3>

4강 , @Component와 컴포넌트 스캔

@Service , @Repository 어노테이션을 붙이면서 빈으로 등록을 할 수 있었어. 이게 어떻게 동작을 하는거냐면, @ComponentScan 어노테이션이 붙어있기 때문인데.

ComponentScan 여기서 가장 중요한 설정이 basePackages 야. 

```
String[] scanBasePackages() default {};
```

보면 문자열인데, 문자열은 type safe 하지 않아. 그래서 basePackageClasses 속성이 있어.

```
Class<?>[] scanBasePackageClasses() default {};
```

이 값을 주면 여기에 전달된 클래스 기준으로 컴포넌트 스캔을 시작하는 거야. 그러니까 바깥의 패키지는 스캔이 안 된다는 거지.

![1643578587310](https://user-images.githubusercontent.com/43261300/151719003-98f4c897-0b36-44a4-bd4b-9cd90046727b.png)

체크된 부분이 기준인데 out 패키지에 있는 MyService 는 어노테이션을 선언해도 등록이 안돼.

또 중요한게 @Filter 야.

컴포넌트 스캔을 한다고 해서 모든 어노테이션을 빈으로 등록해주는건 아니야. 걸러주는 옵션이 있어.

```java
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
```

여기서는 기본적으로 excludeFilters 사용하고 있어. includeFilters 도 있고 여러개 있음. 위에서는 TypeExcludeFilter 랑 AutoConfigurationExcludeFilter 를 걸러내는거야.

그니까 컴포넌트 스캔에서 중요한 게 어디부터 어디까지 스캔한다랑 스캔하는 중에 어떤 것을 걸러낼 거다. 라는 설정이 있다.

기본적으로는 컴포넌트를 들고 있는 어노테이션들이 빈으로 등록이 되는데, 단점은 싱글톤 scope 인 빈들은 초기에 생성을 해. 그렇기 때문에 구동시간이 오래 걸릴 수 있는 거야. 

만약 구동시간이 짧았으면 좋겠다. 싶으면 다른 방법을 고려해야 하는 거지.

스프링 최신버전인 5 에서는 펑션을 사용한 빈 등록 방법임. 이 방법 같은 경우, 리플렉션, 프록시 기반 CGLIB 같은 기법을 사용하지 않기 때문에  성능상의 이점이 존재함. 리플렉션이나 프록시를 만드는 기법은 성능에 영향을 줌.

펑션을 사용한 빈 등록

```java
public static void main(String[] args) {
		//인스턴스를 만들어서 사용하는 방법
		var app = new SpringApplication(CoreSpringApplication.class);
		//중간에 뭔가를 하고 싶다.
		app.addInitializers(new ApplicationContextInitializer<GenericApplicationContext>() {
			@Override
			public void initialize(GenericApplicationContext ctx) {
				//직접 빈을 등록할 수 있어.
				ctx.registerBean(MyService.class); //컴포넌트 스캔 범위 밖에 생성한 클래스를 등록
				ctx.registerBean(ApplicationRunner.class, new Supplier<ApplicationRunner>() {
					@Override
					public ApplicationRunner get() {
						return new ApplicationRunner() {
							@Override
							public void run(ApplicationArguments args) throws Exception {
								System.out.println("Functional Bean Definition!!");
							}
						};
					}
				});
			}
		});
		app.run(args);
}
```

근데 여기서 lambda를 사용하게 되면

```java
app.addInitializers((ApplicationContextInitializer<GenericApplicationContext>) ctx -> {
			//직접 빈을 등록할 수 있어.

    if(true) //1. 추가적인 코딩 가능
    ctx.registerBean(MyService.class); //컴포넌트 스캔 범위 밖에 생성한 클래스를 등록
			ctx.registerBean(ApplicationRunner.class, () -> args1 -> System.out.println("Functional Bean Definition!!"));
		});
```

이렇게 됨. 

위와같이 빈 두 개를 펑셔널 하게 등록함. 이럴 때의 장점은 1. 처럼 코딩을 추가로 할 수 있다는 거임.

또는 빌더를 사용해서도 할 수 있음.

```java
public static void main(String[] args) {
				//빌더를 사용하는 방법
		new SpringApplicationBuilder()
				.sources(CoreSpringApplication.class)
				.initializers((ApplicationContextInitializer<GenericApplicationContext>)
						applicationContext -> {
							applicationContext.registerBean(MyService.class);
						})
				.run(args);
	}
```

컴포넌트스캔의 실제 스캐닝은 ConfigurationClassPostProcessor라는 BeanFactoryPostProcessor에
의해 처리 됨. BeanPostProcessor 와 비슷하지만 실행되는 시점이 다름.

다른 모든 빈들을 만들기 이전에 적용함. 그 말은 빈을 등록하기 전에 컴포넌트 스캔을 해서 빈으로 등록을 한다는 얘기겠지.

