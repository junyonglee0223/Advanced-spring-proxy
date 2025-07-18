# Advanced-spring-proxy

---
# 250409
## [Proxy Pattern and Decorator Pattern]

### Sample project version 1

프로젝트 구조는 다음과 같습니다.

- `src/main/java/hello/proxy/app`
  - `v1`
    - `OrderRepositoryV1` (Interface)
    - `OrderRepositoryV1Impl` (Class)
    - `OrderServiceV1` (Interface)
    - `OrderServiceV1Impl` (Class)
    - `OrderControllerV1` (Interface)
    - `OrderControllerV1Impl` (Class)
- `src/main/java/hello/proxy/config`
  - `AppV1Config` (Configuration)

### OrderRepositoryV1, OrderRepositoryV1Impl
- `OrderRepositoryV1`  
  - `save(String itemId)` 메서드를 선언한다.
  - 파라미터로 넘어온 `itemId`가 `"ex"`인 경우 `IllegalArgumentException`을 발생시키도록 한다.
- `OrderRepositoryV1Impl`  
  - `OrderRepositoryV1`를 구현한다.
  - `save(String itemId)`에서 실제 저장 로직을 구현하고, `Thread.sleep()`을 통해 딜레이를 발생시킨다.
    - `ex`가 입력될 경우 예외 발생,  
    - `sleep` 중 인터럽트 발생 시 `printStackTrace()`로 예외 내용을 출력한다.

### OrderServiceV1, OrderServiceV1Impl
- `OrderServiceV1`  
  - `orderItem(String itemId)` 메서드 선언
- `OrderServiceV1Impl`  
  - `OrderServiceV1`를 구현한다.
  - `orderItem(String itemId)` 안에서 Repository의 `save(itemId)`를 호출한다.
  - 생성자 주입을 통해 `OrderRepositoryV1` 참조를 할당받는다.

### OrderControllerV1, OrderControllerV1Impl
- `OrderControllerV1`  
  - 인터페이스에 `@RequestMapping`, `@ResponseBody` 애노테이션 설정 (스프링 MVC 인식용)
  - `request(String itemId)`, `noLog()` 메서드 선언
- `OrderControllerV1Impl`  
  - 인터페이스를 구현한다.
  - 이전 방식(과거 버전)에서는 인터페이스에 `@RequestMapping`만 있어도 충분했지만,  
    현재 버전에서는 구현체에도 직접 `@Controller`(혹은 `@RestController`) 또는 `@RequestMapping`을 설정해야 정상적으로 인식된다.
  - 생성자 주입을 통해 `OrderServiceV1`를 주입받고, `request()` 메서드에서 Service를 호출한다.
  - URL 매핑:  
    - `GET /v1/request?itemId=hello` → `request(itemId)` 호출  
    - `GET /v1/no-log` → `noLog()` 호출  

### AppV1Config
- 수동으로 `@Bean`을 등록한다.
- 과거 스프링 버전에서는 `@RequestMapping`만으로도 컨트롤러가 인식되었지만,  
  현재는 `@Controller`(또는 `@RestController`)를 구현체에 직접 붙여줘야 Spring MVC가 정상적으로 인식하는 경우가 많다.

### Main Application
- `scanBasePackages = "hello.proxy.app"`: `hello.proxy.app` 이하 패키지들을 컴포넌트 스캔 대상 범위로 지정한다.
- `@Import(AppV1Config.class)`: `AppV1Config`를 스프링 빈 설정으로 추가한다.

### 주의 사항
1. **인터페이스만으로는 스프링 빈이 등록되지 않는다.**
    - 구현체가 있어야 객체를 생성할 수 있고, 그 구현체에 `@Controller`나 `@RestController` 같은 애노테이션을 달아주어야 스프링 MVC가 핸들러(Controller)로 인식한다.
2. **현재 버전에서는 `@RequestMapping`이 클래스 레벨에만 있으면 컨트롤러로 인식하지 않을 수 있다.**
    - 과거 버전과 달리, `@Controller`(or `@RestController`)를 직접 명시해주는 것을 권장한다.
3. **스프링 자동 스캔과 수동 등록**
    - `@Configuration + @Bean` 방식으로 컨트롤러, 서비스, 리포지토리를 등록하더라도, MVC 컨트롤러로 동작시키려면 “구현 클래스에 `@Controller`(`@RestController`)가 필요하다”는 점을 잊지 말아야 한다.



---
## [Proxy Pattern and Decorator Pattern] (cont)

### V2 수동 빈 등록 버전

- `src > ... > app > v2`  
  클래스만으로 구성된 V2 버전은 인터페이스 없이 직접 구현한 `Controller`, `Service`, `Repository` 클래스를 포함합니다.

- `AppV2Config` 클래스  
  `src > ... > config` 경로에 `AppV2Config` 클래스를 생성하여, V2 관련 클래스들을 수동으로 Bean 등록합니다.

- 실행 예시  
  `http://localhost:8080/v2/request?itemId=hello`  


### V3 컴포넌트 스캔 자동 등록 버전

- `RestController`, `Service`, `Repository` 애너테이션을 활용하여 Bean을 자동 등록합니다.

- Component Scan이 활성화되어 있어 `@Component` 계열 애너테이션이 부착된 클래스들이 자동으로 Spring Context에 등록됩니다.

- V2와 달리 별도의 설정 클래스 없이 구성 요소들이 자동으로 인식됩니다.

- 실행 예시  
  `http://localhost:8080/v3/request?itemId=hello`  

---
# 250411
## [Proxy Pattern and Decorator Pattern] (cont)

### Proxy Pattern 테스트 예제

본 예제는 프록시 패턴(Proxy Pattern)을 간단하게 테스트하기 위한 코드 구성과 실행 결과를 정리한 내용입니다. 프록시 패턴을 통해 실제 객체의 호출을 제어하거나 기능을 확장하는 구조를 확인할 수 있습니다.

### 디렉터리 구조

```
test > ... > pureproxy > proxy > code
```

### 1. Subject 인터페이스

- **역할**: 클라이언트와 실제 객체(RealSubject), 프록시(CacheProxy)가 구현해야 하는 공통 인터페이스
- **메서드**
  - `String operation()`

### 2. RealSubject 클래스

- **구현**: `Subject` 인터페이스를 구현한 실제 객체
- **operation 메서드 로직**
  - 호출 로그 출력 (`real subject call!!`)
  - `Thread.sleep()`으로 지연 발생
  - 문자열 반환 (임의값)
- **sleep 메서드**: 테스트 목적으로 일정 시간 대기

### 3. ProxyPatternClient 클래스

- **역할**: 클라이언트 역할
- **구성**
  - `Subject` 타입의 필드를 선언하고 생성자를 통해 주입
  - `execute()` 메서드에서 `operation()` 호출

### 4. ProxyPatternTest 클래스

- **noProxyTest 메서드**
  - `RealSubject` 생성
  - `ProxyPatternClient`에 주입
  - `client.execute()` 3회 실행

**실행 결과**
```
12:40:56.498 [Test worker] INFO RealSubject -- real subject call!!
12:40:57.509 [Test worker] INFO RealSubject -- real subject call!!
12:40:58.514 [Test worker] INFO RealSubject -- real subject call!!
```

- 호출마다 실제 객체가 실행됨을 확인할 수 있음

### 5. CacheProxy 클래스

- **구현**: `Subject` 인터페이스 상속
- **역할**: 캐시 프록시. 결과를 한 번만 계산하고 이후에는 캐시된 값을 반환
- **구성**
  - `Subject target`: 실제 객체를 참조
  - `String cacheValue`: 결과 캐시
- **operation 메서드 로직**
  - 로그 출력 (`proxy call!!`)
  - `cacheValue`가 `null`인 경우 `target.operation()` 실행 및 결과 저장
  - 저장된 `cacheValue`를 반환

### 6. cacheProxyTest 메서드

- `RealSubject` 생성
- `CacheProxy` 생성 후 `RealSubject` 주입
- `ProxyPatternClient`에 `CacheProxy` 주입
- `client.execute()` 3회 실행

**실행 결과**
```
12:54:16.965 [Test worker] INFO CacheProxy -- proxy call!!
12:54:16.976 [Test worker] INFO RealSubject -- real subject call!!
12:54:17.977 [Test worker] INFO CacheProxy -- proxy call!!
12:54:17.979 [Test worker] INFO CacheProxy -- proxy call!!
```

- 첫 번째 호출만 실제 객체가 실행되고 이후는 캐시 결과를 반환하여 성능 최적화를 확인할 수 있음


---
## [Proxy Pattern and Decorator Pattern] (cont)
### Decorator Pattern 테스트 예제

본 예제는 데코레이터 패턴(Decorator Pattern)을 활용하여 객체에 기능을 동적으로 추가하는 방법을 테스트한 내용입니다. 기본 컴포넌트에 다양한 데코레이터를 중첩하여 새로운 기능을 확장하는 구조를 실습했습니다.

### 디렉터리 구조

```
test > ... > pureproxy > decorator > code
```

### 1. Component 인터페이스

- **역할**: 클라이언트가 사용할 공통 인터페이스
- **메서드**
  - `String operation()`

### 2. RealComponent 클래스

- **구현**: `Component` 인터페이스의 실제 구현체
- **operation 로직**
  - 로그 출력 (`RealComponent processing!!`)
  - 문자열 "data" 반환

### 3. DecoratorPatternClient 클래스

- **역할**: 클라이언트 역할
- **구성**
  - `Component` 타입 필드를 생성자로 주입
  - `execute()` 메서드에서 `operation()` 호출 결과를 로그로 출력

### 4. DecoratorPatternTest 클래스 - 기본 테스트

- **noDecorator 메서드**
  - `RealComponent` 인스턴스 생성
  - 클라이언트에 주입 후 `execute()` 실행

**실행 결과**
```
INFO RealComponent -- RealComponent processing!!
INFO DecoratorPatternClient -- result = data
```

### 5. MessageDecorator 클래스

- **역할**: 결과 문자열을 꾸며주는 데코레이터
- **구성**
  - `Component` 필드 주입
  - 내부에서 `component.operation()` 실행 후 결과에 `*****` 문자열 추가
  - 로그 출력 (처리 전, 처리 후)

**operation 결과 예시**
```
INFO MessageDecorator -- MessageDecorator processing!!
INFO RealComponent -- RealComponent processing!!
INFO DecoratorPatternClient -- result = *****data*****
```

### 6. TimeDecorator 클래스

- **역할**: 처리 시간 측정 기능 추가
- **구성**
  - `Component` 필드 주입
  - `operation()` 실행 전후 시간 측정
  - 결과 시간을 로그로 출력

**중첩 구조**  
`TimeDecorator → MessageDecorator → RealComponent`

**실행 흐름 및 결과**
```
INFO TimeDecorator -- TimeDecorator processing!!
INFO MessageDecorator -- MessageDecorator processing!!
INFO RealComponent -- RealComponent processing!!
INFO TimeDecorator -- resultTime = 3
INFO DecoratorPatternClient -- result = *****data*****
```
---
## [Proxy Pattern and Decorator Pattern] (cont)
### Proxy 패턴 적용을 위한 Controller 구조 변경 및 동작 이해

본 프로젝트는 기존 order 프로젝트 구조에 Proxy 기반의 로그 추적 기능을 추가하며, 특히 Controller 레벨에서 프록시를 적용하고 Spring MVC에서 정상 인식되도록 처리하는 방식을 설명합니다.

### 디렉터리 구조

```
src > ... > proxy > config > v1_proxy > interface_proxy
```

### 구현 흐름 요약

#### 1. Proxy 클래스 생성

- OurRepositoryInterfaceProxy: OrderRepositoryV1 상속
- OurServiceInterfaceProxy: OrderServiceV1 상속
- OurControllerInterfaceProxy: OrderControllerV1 상속

각 Proxy 클래스에서는 내부에 target(원본 구현체)과 LogTrace를 필드로 두고,  
logTrace.begin(), logTrace.end() 사이에 타겟 메서드를 실행하며 예외 처리를 포함합니다.

#### 2. InterfaceProxyConfig 클래스 설정
- @Configuration을 통해 프록시 객체들을 직접 Bean으로 등록합니다.
- 원본 구현체는 단순 new로 생성하고, Spring Bean으로 등록되지 않습니다.

#### 3. Controller 프록시의 정상 동작을 위한 핵심 포인트

- 인터페이스(OrderControllerV1)에 @GetMapping, @RequestParam 등 요청 매핑 정보를 선언합니다.
- 프록시 클래스(OurControllerInterfaceProxy)에는 반드시 @RestController를 선언해야 Spring MVC가 해당 객체를 Controller로 인식합니다.

### 왜 @RestController를 프록시 클래스에 붙여야 하는가?

1. Spring MVC는 요청 매핑(@RequestMapping 등)을 인식하려면, 해당 Bean이 Controller 역할을 한다는 표시가 필요합니다.
2. @Bean으로 등록된 객체라도, 클래스에 @RestController 또는 @Controller가 없으면 매핑을 처리하지 않습니다.
3. 따라서 @RestController가 없으면 404, No mapping found와 같은 에러가 발생합니다.

### 충돌이 발생하지 않는 이유

- 흔한 충돌 사례:
  1. 동일한 Bean 이름 중복
  2. 동일한 매핑 경로 중복

- 본 구조에서는 다음과 같은 이유로 충돌이 없습니다:
  - @ComponentScan으로 자동 등록하지 않음
  - 원본 컨트롤러(OrderControllerV1Impl)는 Spring Bean으로 등록되지 않음
  - 오직 하나의 컨트롤러 Bean(OurControllerInterfaceProxy)만 등록됨

### @RestController + @Bean 조합의 의미

- 컴포넌트 스캔 없이도 @Bean으로 등록된 객체가, 클래스에 @RestController가 붙어 있으면 → Spring MVC가 해당 객체를 컨트롤러로 인식합니다.
- 결과적으로, 프록시 클래스가 Controller 역할과 AOP 역할을 동시에 수행하게 됩니다.

### 결론 요약

1. @RestController를 제거하면 → 더 이상 컨트롤러로 인식되지 않아 매핑 에러(404 등)가 발생합니다.
2. 충돌이 안 나는 이유는 → 동일 클래스를 중복 등록하거나, 동일 매핑 경로를 가진 다른 컨트롤러가 없기 때문입니다.
3. Bean 등록 방식은 @Bean 메서드로 수동 등록이지만, 클래스에 있는 @RestController가 그 Bean이 Controller 역할임을 Spring에 알려줍니다.
4. Spring MVC가 정상적으로 매핑을 찾아서 동작하려면,
  - 프록시 클래스가 컨트롤러 애노테이션(@Controller/@RestController)을 갖고,
  - 인터페이스(OrderControllerV1)에 매핑 정보(@GetMapping)를 선언해야 합니다.

### 로그 결과 예시

```
INFO ThreadLocalLogTrace : [beae3f02] OrderController.request()
INFO ThreadLocalLogTrace : [beae3f02] |-->OrderService.orderItem()
INFO ThreadLocalLogTrace : [beae3f02] | |-->OrderRepository.save()
INFO ThreadLocalLogTrace : [beae3f02] | |<--OrderRepository.save() time = 1007ms
INFO ThreadLocalLogTrace : [beae3f02] |<--OrderService.orderItem() time = 1008ms
INFO ThreadLocalLogTrace : [beae3f02] OrderController.request() time = 1011ms
```

Controller → Service → Repository로 프록시가 모두 연결되고, 로그가 트랜잭션처럼 이어져 출력됩니다.

이 구조를 통해, Spring MVC 컨트롤러에도 프록시 기반 AOP를 손쉽게 적용할 수 있으며,  
핵심은 "컨트롤러 역할임을 인식시킬 애노테이션과, Bean 등록 경로의 명확한 분리"입니다.

---

# 250415
## [Proxy Pattern and Decorator Pattern] (cont)
### Proxy 패턴 - 구체 클래스 기반 적용 예제

본 예제는 인터페이스가 없는 구체 클래스(Concrete Class)를 대상으로 Proxy 패턴을 적용하는 방식입니다. `ConcreteLogic`이라는 단일 구현체를 프록시로 감싸서 기능을 확장하며, 특히 실행 시간 측정을 위한 `TimeProxy` 프록시를 구현했습니다.

### 디렉터리 구조

```
test > ... > pureproxy > concreteproxy > code
```

### 기본 구조

### 1. ConcreteLogic 클래스

- 메서드: `String operation()`
- 기능: 로그 출력 (`concrete logic operation!!`) 후 "data" 반환

### 2. ConcreteClient 클래스

- 필드: `ConcreteLogic` 객체
- 생성자 주입 방식으로 주입
- `execute()` 메서드에서 `concreteLogic.operation()` 실행

### 3. ConcreteProxyTest - noProxy 테스트

**출력 결과**
```
INFO ConcreteLogic -- concrete logic operation!!
```

### 프록시 적용

### 4. TimeProxy 클래스

- `ConcreteLogic`을 `extends`하여 프록시 역할 수행
- 내부에 `ConcreteLogic realLogic` 필드를 주입
- `operation()` 오버라이드:
  - 시작 로그, 시간 측정
  - 실제 로직 위임 (`realLogic.operation()`)
  - 종료 로그 및 실행 시간 출력
  - 결과 반환


### 5. ConcreteProxyTest - addProxy 테스트

**출력 결과**
```
INFO TimeProxy -- time proxy start!!
INFO ConcreteLogic -- concrete logic operation!!
INFO TimeProxy -- time proxy end!!, resultTime = 0ms
```
---

## [Proxy Pattern and Decorator Pattern] (cont)
### 구체 클래스 기반 Proxy 패턴 - 기존 Order 앱에 적용

기존 `order` 앱 구조에 구체 클래스 기반(Concrete Class) 프록시 패턴을 적용하여 `logTrace` 기능을 확장하였습니다. 인터페이스가 아닌 구현체(`extends`) 중심으로 프록시를 설계하였으며, 각 계층(Repository, Service, Controller)에 대해 개별 프록시 클래스를 생성하고 설정 파일을 통해 수동 등록하는 방식으로 구성하였습니다.

### 디렉터리 구조

```
src > ... > config > v1_proxy > concrete_proxy
```

### Proxy 클래스 구현

### 1. OrderRepositoryConcreteProxy

- `OrderRepositoryV2`를 `extends`로 상속
- `OrderRepositoryV2`(target)와 `LogTrace`를 생성자로 주입 (`final`)
- `save(String itemId)` 메서드를 오버라이드하여 `logTrace.begin`, `logTrace.end`, `exception` 처리 포함

### 2. OrderServiceConcreteProxy

- `OrderServiceV2`를 `extends`
- `super(null)` 호출 필수: 자식 클래스가 생성되면 부모 클래스의 생성자도 호출되어야 하기 때문
- 하지만 실제 로직은 target 객체로 처리되므로 `null` 전달 (사용되지 않음)
- `orderItem()` 메서드 오버라이드하여 로그 추적 수행

### 3. OrderControllerConcreteProxy

- `OrderControllerV2`를 `extends`
- 로그 추적을 프록시에서 수행
- 기존 컨트롤러에서 사용하던 `@RestController` 애노테이션을 프록시로 옮김

### 설정 클래스: ConcreteProxyConfig


### 실행 결과

```
INFO ThreadLocalLogTrace : [12200e0b] OrderController.request()
INFO ThreadLocalLogTrace : [12200e0b] |-->OrderService.orderItem()
INFO ThreadLocalLogTrace : [12200e0b] | |-->OrderRepository.save()
INFO ThreadLocalLogTrace : [12200e0b] | |<--OrderRepository.save() time = 1005ms
INFO ThreadLocalLogTrace : [12200e0b] |<--OrderService.orderItem() time = 1007ms
INFO ThreadLocalLogTrace : [12200e0b] OrderController.request() time = 1010ms
```

프록시를 통해 Controller → Service → Repository로 이어지는 로그 추적이 모두 정상적으로 수행됨을 확인할 수 있습니다.

### 의문점: `@RestController`를 원본 또는 프록시에 설정해도 정상 동작하는 이유

- 해당 프록시 구조는 `extends` 기반이므로 **프록시도 부모 클래스의 모든 매핑과 로직을 그대로 상속**합니다.
- 즉, `@RestController`가 프록시 클래스에 있으면 Spring MVC는 프록시를 컨트롤러로 인식하고,
  부모 클래스(`OrderControllerV2`)에 선언된 `@GetMapping` 정보를 그대로 읽어 사용합니다.
- 반대로, 원본 클래스에 `@RestController`가 있고 프록시는 단순 객체로만 등록되는 경우에도,
  Spring MVC가 **원본 클래스를 컨트롤러로 등록**하게 되어 정상 동작합니다.
- 하지만 **프록시를 빈으로 등록할 경우**, `@RestController`는 반드시 **프록시 클래스에 선언되어야** Spring MVC가 요청 매핑을 인식합니다.
- 따라서 실제 동작을 원하는 방식대로 조절하려면 `@RestController`는 프록시 클래스에 선언하고, **원본 클래스에서는 제거하는 것이 명확한 설계**입니다.



---
# 250416
## [Dynamic Proxy Technique]
### Reflection 기반 동적 호출 테스트 및 개념 정리

기존 Proxy 설정 방식은 MVC 계층별로 Proxy 클래스를 하나씩 만들어 직접 설정해야 하므로,  
구현 대상이 많아질수록 코드 중복과 설정 비용이 급격히 증가하는 단점이 존재한다.  
이러한 반복 비용을 줄이기 위해 **동적 프록시(Dynamic Proxy)** 개념이 필요하다.  
대표적으로는 **JDK 동적 프록시**와 **CGLIB**이 있으며, 이를 이해하기 위해서는 **Reflection(리플렉션)** 에 대한 기본 이해가 선행되어야 한다.

### 테스트 구조

```
test > ... > proxy > jdkdynamic > ReflectionTest.java
```

### reflection0 테스트

- 목적: 기본 Hello 클래스의 직접 호출 테스트
- 테스트 흐름:
  1. Hello 객체 생성
  2. `callA()`, `callB()` 메서드 직접 호출
  3. 로그 확인

#### Hello 클래스

#### 실행 로그

```
INFO ReflectionTest -- call A starts!!
INFO Hello -- call A!!
INFO ReflectionTest -- result = A
INFO ReflectionTest -- call B starts!!
INFO Hello -- call B!!
INFO ReflectionTest -- result = B
```

### reflection1 테스트

- 목적: Reflection을 이용한 메서드 호출
- 주요 포인트:
  - `Class.forName()`으로 클래스 로드
  - `getMethod()`로 메서드 객체 획득
  - `invoke()`로 메서드 실행

#### 실행 로그

```
INFO ReflectionTest -- call A starts!!
INFO Hello -- call A!!
INFO ReflectionTest -- result = A
```

- 기존 테스트(`reflection0`)와 동일한 결과가 출력되지만, 호출 방식은 동적으로 변경됨

### 리팩터링: 공통 로직 분리 (dynamicCall 메서드)

- 목적: 메서드 호출 전후의 공통 로직을 메서드로 추출
- 메서드 시그니처: `void dynamicCall(Method method, Object target)`


#### 실행 로그

```
INFO ReflectionTest -- start!!
INFO Hello -- call A!!
INFO ReflectionTest -- result = A
INFO ReflectionTest -- start!!
INFO Hello -- call B!!
INFO ReflectionTest -- result = B
```

### 주의 사항

- **Reflection은 컴파일 타임에 오류를 잡지 못한다.**
- 잘못된 메서드명, 시그니처 등은 **런타임 예외**로 이어질 수 있음
- 따라서 Reflection은 일반적인 로직에서는 지양되며, 프레임워크, 테스트, 프록시 생성 등 **제어 역전이 필요한 내부 기술에서 주로 사용**

---
## [Dynamic Proxy Technique] (cont)
### JDK Dynamic Proxy 기반 Proxy 적용 테스트

이 예제는 JDK 동적 프록시를 활용해 런타임 시 인터페이스 기반 프록시 객체를 생성하고, 공통 기능(예: 실행 시간 측정)을 적용하는 구조를 설명한다.  
JDK 동적 프록시는 반드시 **인터페이스 기반으로만 프록시 객체를 생성**할 수 있으므로, 대상 객체는 인터페이스를 구현하고 있어야 한다.

### 테스트 구성

- `AInterface`, `BInterface`: 각 인터페이스는 `call()` 메서드를 하나씩 정의
- `AImpl`, `BImpl`: 인터페이스를 구현하며 내부에서 간단한 로그 출력
- `TimeInvocationHandler`: 공통 부가기능으로 메서드 실행 시간 측정 및 로그 출력 수행

### 프록시 생성 흐름

1. 대상 객체(AImpl 또는 BImpl) 생성
2. 대상 객체를 감싼 InvocationHandler 생성
3. `Proxy.newProxyInstance()`를 사용해 프록시 객체 생성
4. 프록시 객체를 통해 메서드 실행

### 실행 결과 예시 (AInterface)

```
13:05:53.028 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- Time proxy starts!!
13:05:53.036 [Test worker] INFO hello.proxy.jdkdynamic.code.AImpl -- call A!!
13:05:53.037 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- Time proxy end!! resultTime = 1ms
13:05:53.041 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- target Class = class hello.proxy.jdkdynamic.code.AImpl
13:05:53.042 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- proxy Class = class jdk.proxy3.$Proxy12
```

### 실행 결과 예시 (BInterface)

```
13:45:33.361 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- Time proxy starts!!
13:45:33.370 [Test worker] INFO hello.proxy.jdkdynamic.code.BImpl -- call B!!
13:45:33.371 [Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- Time proxy end!! resultTime = 1ms
13:45:33.376 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- target Class = class hello.proxy.jdkdynamic.code.BImpl
13:45:33.377 [Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- proxy Class = class jdk.proxy3.$Proxy12
```

### 정리
- JDK 동적 프록시는 인터페이스 기반으로만 프록시 객체를 생성할 수 있다.
- `InvocationHandler`를 통해 모든 메서드 호출에 공통 기능을 적용할 수 있다.
- 런타임에 생성되는 프록시 객체는 `$ProxyXX` 형태로 나타나며, 실제 클래스가 아님을 로그로 확인할 수 있다.
- 코드 중복 없이 다양한 대상에 공통 기능을 적용하고 싶을 때 유용하게 활용된다.


---
## [Dynamic Proxy Technique] (cont)
### JDK Dynamic Proxy 기반 로그 추적 기능 Order 프로젝트에 적용

기존 Order 프로젝트에 JDK 동적 프록시 기반의 공통 로그 추적 기능을 적용하였다.  
프록시 객체를 직접 생성하고 `InvocationHandler`를 구현하여 Service, Repository 계층에 공통 기능을 위임하였다.  
단, Controller는 Spring MVC에서 `@RestController` 인식을 위해 기존 인터페이스 기반 Proxy 방식을 유지하였다.

### 디렉터리 구조

```
src > ... > config > v2_dynamicproxy > handler
src > ... > config > v2_dynamicproxy
```

### 로그 추적 핸들러 구현

#### LogTraceBasicHandler 클래스

- `InvocationHandler`를 구현
- 프록시 대상 객체(`target`)와 로그 추적 객체(`logTrace`)를 주입받아 실행
- `invoke()` 메서드 내에서 다음 로직 수행:
  - 로그 시작: `logTrace.begin()`
  - 메서드 호출: `method.invoke(target, args)`
  - 로그 종료: `logTrace.end()`
  - 예외 발생 시 `logTrace.exception()`으로 처리
- 로그 메시지는 `클래스명.메서드명()` 형식으로 구성

### 설정 클래스 구성

#### DynamicProxyBasicConfig 클래스

- `@Configuration` 클래스로, 프록시 객체를 직접 생성해 Bean으로 등록
- `Proxy.newProxyInstance()`를 사용하여 `OrderRepositoryV1`, `OrderServiceV1` 프록시 생성
- 각 프록시의 핸들러로 `LogTraceBasicHandler`를 설정하여 공통 로그 기능을 적용

### Controller 처리 방식

- `OrderControllerV1`의 경우 Spring MVC가 `@RestController`가 선언된 Bean을 인식해야 하기 때문에,  
  기존에 구현한 인터페이스 기반 프록시 방식(`OrderControllerInterfaceProxy`)을 그대로 유지


### 실행 결과 로그

```
INFO ThreadLocalLogTrace : [78f23142] OrderController.request()
INFO ThreadLocalLogTrace : [78f23142] |-->OrderServiceV1.orderItem()
INFO ThreadLocalLogTrace : [78f23142] | |-->OrderRepositoryV1.save()
INFO ThreadLocalLogTrace : [78f23142] | |<--OrderRepositoryV1.save() time = 1015ms
INFO ThreadLocalLogTrace : [78f23142] |<--OrderServiceV1.orderItem() time = 1019ms
INFO ThreadLocalLogTrace : [78f23142] OrderController.request() time = 1023ms
```

---
## [Dynamic Proxy Technique] (cont)
### CGLIB 기반 클래스 프록시 적용 테스트

CGLIB은 원래 외부 라이브러리였으나, Spring Framework에 통합되어 내부에 포함된 바이트코드 조작 기술이다.  
인터페이스 없이도 클래스 기반으로 프록시 객체를 생성할 수 있다는 장점이 있으며, 스프링 AOP나 ProxyFactory 같은 고수준 기술에서 내부적으로 사용된다.  
직접 사용하는 경우는 드물고, 개념적인 이해만으로도 충분한 경우가 많다.

### 테스트 구성

디렉터리 구조:

```
test > ... > proxy > common > service
test > ... > proxy > cglib > code
```

#### Service 구성

- `ServiceInterface`: `save()`, `find()` 메서드를 선언
- `ServiceImpl`: `ServiceInterface` 구현, 메서드 호출 시 로그 출력
- `ConcreteService`: 인터페이스 없이 단독 클래스, 내부 메서드에서 단순 로그 출력

#### 프록시 핸들러 구현

- `TimeMethodInterceptor`: `MethodInterceptor`를 상속
  - `target` 객체를 필드로 가지고, 생성자를 통해 주입받음
  - `intercept()` 메서드 내에서
    - 시작 로그 출력
    - 실제 대상 메서드 호출
    - 종료 로그 및 수행 시간 출력
  - `InvocationHandler`와 구조적으로 유사함

#### 테스트 클래스

- `CglibTest` 클래스의 `cglib()` 테스트 메서드에서 프록시 생성 흐름 수행
- `ConcreteService` 객체를 대상으로 프록시 생성

1. `Enhancer` 생성
2. `setSuperclass()`로 대상 클래스 지정
3. `setCallback()`으로 `TimeMethodInterceptor` 설정
4. `enhancer.create()`로 실제 프록시 객체 생성
5. 프록시 객체 메서드 호출

### 실행 로그 예시

```
17:15:26.334 [Test worker] INFO hello.proxy.cglib.CglibTest -- target Class = class hello.proxy.common.service.ConcreteService
17:15:26.349 [Test worker] INFO hello.proxy.cglib.CglibTest -- proxy Class = class hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$48bd19d7
17:15:26.349 [Test worker] INFO hello.proxy.cglib.code.TimeMethodInterceptor -- Time method start!!
17:15:26.350 [Test worker] INFO hello.proxy.common.service.ConcreteService -- concrete service call!!
17:15:26.351 [Test worker] INFO hello.proxy.cglib.code.TimeMethodInterceptor -- Time method end!!
```

### 정리

- CGLIB은 인터페이스 없이 클래스만으로도 프록시를 생성할 수 있다는 장점이 있다.
- 실제 프록시는 대상 클래스를 상속받아 생성되며, `$$EnhancerByCGLIB$$` 형태의 클래스 이름을 가진다.
- CGLIB은 클래스 기반이기 때문에 `final` 클래스나 `final` 메서드는 프록시가 적용되지 않는다.
- 스프링 내부에서 CGLIB은 AOP 적용 시 널리 사용되며, 실제 개발자는 ProxyFactory, @Transactional 등을 통해 간접적으로 활용한다.
- 이번 예제는 CGLIB의 기본 사용법과 동작 원리를 확인하고 이해하는 데 목적이 있다.


---
# 250417
## [Proxies Supported by Spring]
### ProxyFactory를 활용한 공통 Proxy 설정 및 AOP 적용

JDK 동적 프록시와 CGLIB 프록시 방식은 각각 인터페이스 기반, 클래스 기반으로 동작하며, 상황에 따라 혼용되는 경우가 많다.  
하지만 이때마다 프록시 로직을 따로 구현하고 설정하는 것은 매우 비효율적이다.  
이러한 불편을 해결하기 위해 스프링에서는 **`ProxyFactory`와 `Advice`를 사용한 공통 AOP 적용 방식**을 제공한다.

### 핵심 개념

- `ProxyFactory`는 JDK, CGLIB 여부와 무관하게 **프록시 객체를 생성**할 수 있는 Spring AOP 도구이다.
- `MethodInterceptor`는 **Spring AOP의 Advice 인터페이스**로, 공통 기능을 정의한다.
- Spring은 프록시 생성 시 내부적으로 JDK 프록시를 우선 사용하며, 조건에 따라 CGLIB으로 전환할 수 있다.
- `setProxyTargetClass(true)`를 설정하면 인터페이스가 있어도 **무조건 CGLIB 기반 프록시를 사용**하게 된다.

### 테스트 구성

#### 공통 Advice

- `TimeAdvice` 클래스는 `MethodInterceptor`를 구현하여 공통 기능(Time 측정)을 수행한다.
- 내부에서 `invocation.proceed()`로 실제 타겟 메서드를 호출하고, 전후로 시간을 측정해 로그를 출력한다.

#### ProxyFactoryTest 테스트 클래스

- 다양한 경우의 프록시 생성 방식과 프록시 타입 판별을 테스트한다.
- 프록시 클래스는 `AopUtils`를 통해 AOP 적용 여부, JDK/CGLIB 구분 가능

### interface 기반 프록시 테스트 결과

```
14:59:37.260 [Test worker] INFO ProxyFactoryTest -- targetClass = class ServiceImpl
14:59:37.282 [Test worker] INFO ProxyFactoryTest -- proxyClass = class jdk.proxy3.$Proxy12
14:59:37.307 [Test worker] INFO TimeAdvice -- Time Proxy start!!
14:59:37.308 [Test worker] INFO ServiceImpl -- save call!!
14:59:37.308 [Test worker] INFO TimeAdvice -- Time Proxy finish!!, resultTime = 0
```

- 인터페이스 기반이므로 JDK 프록시가 적용됨
- AOP Proxy: true, JDK Proxy: true, CGLIB Proxy: false

### 클래스 기반 프록시 테스트 결과

```
15:08:37.324 [Test worker] INFO ProxyFactoryTest -- targetClass = class ConcreteService
15:08:37.339 [Test worker] INFO ProxyFactoryTest -- proxyClass = class ConcreteService$$SpringCGLIB$$0
15:08:37.360 [Test worker] INFO TimeAdvice -- Time Proxy start!!
15:08:37.360 [Test worker] INFO ConcreteService -- concrete service call!!
15:08:37.361 [Test worker] INFO TimeAdvice -- Time Proxy finish!!, resultTime = 1
```

- 인터페이스가 없기 때문에 자동으로 CGLIB 프록시가 생성됨
- AOP Proxy: true, JDK Proxy: false, CGLIB Proxy: true

### 인터페이스 대상인데 CGLIB 강제 적용 (`setProxyTargetClass(true)` 사용)

```
15:22:15.965 [Test worker] INFO ProxyFactoryTest -- targetClass = class ServiceImpl
15:22:15.985 [Test worker] INFO ProxyFactoryTest -- proxyClass = class ServiceImpl$$SpringCGLIB$$0
15:22:15.999 [Test worker] INFO TimeAdvice -- Time Proxy start!!
15:22:15.999 [Test worker] INFO ServiceImpl -- save call!!
15:22:15.999 [Test worker] INFO TimeAdvice -- Time Proxy finish!!, resultTime = 0
```

- 인터페이스가 있어도 `setProxyTargetClass(true)` 설정으로 CGLIB 프록시가 사용됨
- AOP Proxy: true, JDK Proxy: false, CGLIB Proxy: true

---
# 250423
## [Proxies Supported by Spring] (cont)
### Pointcut, Advice, Advisor를 활용한 프록시 적용 방식

스프링 AOP에서는 프록시를 설정할 때 **Pointcut**(어디에 적용할지), **Advice**(무엇을 적용할지)를 조합하여 **Advisor**로 구성한다.  
Advisor는 ProxyFactory가 프록시 생성 시 사용할 수 있도록 도와주는 핵심 개념이다.  
Pointcut + Advice = Advisor 형태로 이해하면 된다.

### 테스트 구조 및 목적

```
test > ... > proxy > advisor
```

테스트 목적은 아래와 같다:

- 공통 로직을 Advice로 추출하고
- 어디에 적용할지 Pointcut으로 정의한 후
- 이 둘을 Advisor로 결합하여 ProxyFactory에 등록한다

### 기본 테스트: 모든 메서드에 프록시 적용

- Pointcut을 항상 true를 반환하도록 설정
- Advisor로 `DefaultPointcutAdvisor` 사용
- Advice는 `TimeAdvice`를 사용하여 실행 시간 로그 측정

**실행 결과**

```
Time Proxy start!!
save call!!
Time Proxy finish!!
Time Proxy start!!
find call!!
Time Proxy finish!!
```

`save()`와 `find()` 모두 프록시 적용됨을 확인할 수 있다.

### 커스텀 Pointcut 적용

- `Pointcut`을 직접 구현하여 `save` 메서드만 선택적으로 프록시 적용
- `MethodMatcher` 내부에서 메서드 이름이 `save`일 경우만 true 반환

**실행 결과**

```
pointCut call!! targetClass = ..., methodClass = save
pointCut result = true
Time Proxy start!!
save call!!
Time Proxy finish!!

pointCut call!! targetClass = ..., methodClass = find
pointCut result = false
find call!!
```

`save()`만 프록시 적용되고, `find()`는 프록시 적용되지 않음

### 스프링에서 제공하는 Pointcut

- `NameMatchMethodPointcut`: 메서드 이름 기반 매칭
  - `setMappedName("save")`로 설정 시 `save`만 프록시 적용
- `JdkRegexpMethodPointcut`: 정규 표현식 기반
- `TruePointcut`: 항상 true 반환
- `AnnotationMatchingPointcut`: 애노테이션 기반
- `AspectJExpressionPointcut`: AspectJ 표현식 기반으로 가장 널리 사용

**실행 결과 (NameMatchMethodPointcut 사용)**

```
Time Proxy start!!
save call!!
Time Proxy finish!!
find call!!
```

`save()`만 프록시 적용됨

### 다중 Advisor 적용 (프록시 2개 연결 방식)

- `ProxyFactory`를 두 번 사용하여 프록시 체인 구성
- advisor1 → proxy1 생성
- proxy1을 target으로 하는 advisor2 → proxy2 생성

**실행 결과**

```
advice2 call!!
advice1 call!!
save call!!
```

### 다중 Advisor 적용 (하나의 ProxyFactory에 여러 Advisor 등록)

- `proxyFactory.addAdvisor(advisor1)`
- `proxyFactory.addAdvisor(advisor2)`

**실행 결과**

```
advice1 call!!
advice2 call!!
save call!!
```

Advisor는 스택 구조처럼 동작하여, **나중에 등록한 Advisor가 먼저 실행된다**

---
## [Proxies Supported by Spring] (cont)
### Order 프로젝트에 LogTrace ProxyFactory 적용

기존 Order 프로젝트에 공통 로그 추적 기능을 프록시로 적용하기 위해 Spring의 `ProxyFactory`와 `Advice` 구조를 도입하였다.  
프록시 적용 대상은 V1 (interface 기반), V2 (concrete class 기반) 구조 모두 포함되며, 로그 추적은 `LogTraceAdvice`를 통해 수행된다.

### LogTraceAdvice 구현

`org.aopalliance.intercept.MethodInterceptor`를 상속받아 `LogTraceAdvice` 클래스를 구현한다.  
`invoke()` 메서드 내부에서 로그 시작 및 종료 시점을 기록하며, 프록시가 적용된 메서드는 로그 메시지 형식으로 출력된다.  
예: `OrderController.request()`, `OrderServiceV1.orderItem()`, `OrderRepositoryV1.save()`

### V1 - Interface 기반 프록시 적용

구현 위치: `proxy.config.v3_proxyfactory.ProxyFactoryConfigV1`  
대상 클래스: `OrderControllerV1`, `OrderServiceV1`, `OrderRepositoryV1`  
프록시 생성 방식: `ProxyFactory`를 사용하여 JDK 동적 프록시 생성  
포인트컷 설정:

```java
pointcut.setMappedNames("request*", "order*", "save*");
```

메서드 이름이 포인트컷 조건과 일치할 때만 프록시가 적용된다.

**실행 결과 예시**

```
OrderController.request()
|-->OrderServiceV1.orderItem()
| |-->OrderRepositoryV1.save()
| |<--OrderRepositoryV1.save() time = 1010ms
|<--OrderServiceV1.orderItem() time = 1011ms
OrderController.request() time = 1017ms
```

### V2 - Concrete 기반 프록시 적용

구현 위치: `proxy.config.v3_proxyfactory.ProxyFactoryConfigV2`  
대상 클래스: `OrderControllerV2`, `OrderServiceV2`, `OrderRepositoryV2`  
인터페이스 없이도 `ProxyFactory`는 내부적으로 CGLIB을 통해 프록시를 생성한다.  
Controller에도 `@RestController`가 적용되어 있으므로 Spring MVC가 요청 매핑을 정상 인식한다.  
프록시는 CGLIB 클래스(`$$SpringCGLIB$$`)로 생성되며, 메서드 이름이 포인트컷 조건과 일치하면 Advice가 실행된다.

**실행 결과 예시**

```
OrderControllerV2.request()
|-->OrderServiceV2.orderItem()
| |-->OrderRepositoryV2.save()
| |<--OrderRepositoryV2.save() time = 1022ms
|<--OrderServiceV2.orderItem() time = 1029ms
OrderControllerV2.request() time = 1030ms
```

### 주의사항

`NameMatchMethodPointcut`에서 설정한 메서드 이름에 오타가 있거나 대상 클래스에 존재하지 않으면 프록시가 적용되지 않는다.  
`ProxyFactory`를 사용할 경우 인터페이스 여부에 따라 JDK 동적 프록시 또는 CGLIB 프록시로 자동 선택된다.  
Concrete 클래스 기반에서도 `@RestController`는 프록시 객체에 정상 인식되며, HTTP 요청 처리가 가능하다.

---
# 250425
## [Bean Post-Processor]

### Bean Post-Processor 적용 테스트

Spring에서 제공하는 `BeanPostProcessor`는 빈 생성 이후의 로직에 개입하여 빈을 조작하거나 완전히 다른 객체로 바꿔치기하는 등 매우 유연하고 강력한 기능을 제공한다.  
본 테스트에서는 기본적인 빈 등록 테스트부터, 후처리기를 활용해 등록된 빈을 다른 객체로 변경하는 동작까지 확인하였다.

### 1. 기본 빈 등록 테스트

테스트 클래스: `BasicTest`  
등록된 설정 클래스: `BasicConfig`

- `@Bean(name = "beanA")`으로 `A` 클래스를 등록한다.
- `ApplicationContext`를 통해 `"beanA"`라는 이름으로 `A.class` 타입의 빈을 정상 조회한다.
- 반대로 `B.class` 타입으로 빈을 조회할 경우 예외(`NoSuchBeanDefinitionException`)가 발생하는지 확인한다.



### 2. BeanPostProcessor를 이용한 빈 바꿔치기 테스트

테스트 클래스: `BeanPostProcessorTest`  
설정 클래스: `BeanPostProcessorConfig`

- `beanA`라는 이름으로 `A` 객체를 등록했지만, `BeanPostProcessor`를 통해 이를 `B` 객체로 대체한다.
- 후처리 클래스 `AToBPostProcessor`는 `BeanPostProcessor`를 구현하며 `postProcessBeforeInitialization` 또는 `postProcessAfterInitialization`에서 `A` 타입의 객체를 감지하면 새로운 `B` 객체로 교체하여 반환한다.
- 그 결과, `"beanA"`를 `B.class` 타입으로 조회하면 성공하고, `A.class` 타입으로 조회하면 예외가 발생한다.


### 테스트 결과 로그 예시

```
WARN ... BasicConfig ... not eligible for getting processed by all BeanPostProcessors ... consider declaring it as static instead.
INFO AToBPostProcessor -- bean = hello.proxy...A@579d011c, beanName = beanA
INFO B -- helloB
```

경고 메시지는 설정 클래스 `BasicConfig`가 `static`이 아니기 때문에 발생하며, BeanPostProcessor가 해당 클래스에 완전히 적용되지 않을 수 있다는 내용이다. 이는 테스트의 핵심 동작에는 영향을 주지 않지만 구조적으로는 `@Configuration` 클래스를 `static`으로 선언하는 것이 바람직하다.

---
# 250429
## [Bean Post-Processor] (cont)
### v4_postprocessor 빈 후처리기 적용

기존에 테스트했던 빈 후처리기를 프로젝트에 적용하였다.

### PackageLogTraceProxyPostProcessor 클래스 생성

- 위치: `src > … > proxy > config > v4_postprocessor > postprocessor`
- `BeanPostProcessor` 상속 후 `postProcessAfterInitialization` 메서드 override
- 필드
  - `basePackage` (String 타입)
  - `advisor` (Advisor 타입)
- 생성자 주입 방식으로 필드 주입
- `postProcessAfterInitialization` 내부 로직
  - 로그 출력 (beanName, bean)
  - `bean.getClass().getPackageName()`을 통해 packageName 추출
  - `packageName`이 `basePackage`로 시작하지 않으면 원본 `bean` 반환
  - `ProxyFactory` 생성 후 bean 등록, advisor 등록
  - 프록시 객체 생성
  - 생성된 프록시에 대해 로그 출력 (target, proxy)
  - 프록시 객체 반환

### BeanPostProcessorConfig 클래스 생성

- 위치: `src > … > proxy > config > v4_postprocessor`
- `@Configuration` 어노테이션 설정
- `@Import` 어노테이션으로 `AppV1Config.class`, `AppV2Config.class` 주입
- `logTraceProxyPostProcessor` 메서드 생성
  - `PackageLogTraceProxyPostProcessor` 타입 반환
  - `basePackage`는 `"hello.proxy.app"`로 설정
  - `getAdvisor(LogTrace)` 메서드를 통해 advisor 생성 후 주입
- `getAdvisor(LogTrace)` 메서드 내부
  - `NameMatchMethodPointcut` 생성
  - `setMappedNames`로 `"request*"`, `"order*"`, `"save*"` 패턴 설정
  - `LogTraceAdvice` 생성 (LogTrace 주입)
  - `DefaultPointcutAdvisor` 생성하여 반환

### 프록시 적용 결과

- 구체 클래스(concrete class) 기반으로 설정한 경우 정상적으로 proxy 작동함
- 인터페이스 기반 설정 시 오류 발생
  - 예시 로그
    ```
    2025-04-29T14:31:41.661+09:00  INFO 78304 --- [proxy] [nio-8080-exec-2] h.p.c.v.p.PackageLogTraceProxyProcessor  : beanName = error, bean = class org.springframework.web.servlet.view.InternalResourceView
    ```

### 문제 해결 방법

- 첫 번째 방법: Controller 구현체에 `@RestController` 직접 선언하고 `ProxyFactory`에 `setProxyTargetClass(true)` 설정하여 클래스 기반 프록시 강제 사용
- 두 번째 방법: Controller 인터페이스에 `@RestController` 선언하고 `AppV1Config`에 수동으로 Controller 빈 등록


---
# 250501
## [Bean Post-Processor] (cont)
### AOP 자동 프록시 적용을 위한 AutoProxyConfig 설정

* `build.gradle`에 다음 의존성을 추가하여 AOP 설정을 활성화한다:
  `implementation 'org.springframework.boot:spring-boot-starter-aop'`

* 위 의존성을 추가하면 Spring은 AspectJ의 `aspectjweaver` 라이브러리를 통해 AOP 프록시를 자동으로 생성해준다.

### AutoProxyConfig 클래스 생성

* 위치: `src > … > proxy > config > v5_autoproxy`
* `@Configuration` 애노테이션으로 설정 클래스 등록
* `@Import(AppV1Config.class)` 및 `@Import(AppV2Config.class)`로 수동 빈 등록된 설정 클래스 가져오기

### Advisor 설정

* `getAdvisor1(LogTrace logTrace)` 메서드 정의

  * `NameMatchMethodPointcut`을 통해 pointcut 설정
  * 포인트컷 대상 메서드 이름: `"request*"`, `"order*"`, `"save*"`
  * `LogAdvice` 클래스 주입
  * `DefaultPointcutAdvisor`로 pointcut과 advice를 결합

### AOP 적용 확인

* `Main` 클래스에 `AutoProxyConfig`를 import하여 설정 등록
* AOP 로그는 `request`, `order`, `save` 접두어가 붙은 메서드에 대해 자동으로 적용되며 다음과 같은 형식으로 출력된다:

#### `/v1/request?itemId=hello` 요청 결과

```
OrderControllerV1Impl.request()
|-->OrderServiceV1Impl.orderItem()
| |-->OrderRepositoryV1Impl.save()
...
```

#### `/v2/request?itemId=hello` 요청 결과

```
OrderControllerV2.request()
|-->OrderServiceV2.orderItem()
| |-->OrderRepositoryV2.save()
...
```

#### `/v3/request?itemId=hello` 요청 결과

```
OrderControllerV3.request()
|-->OrderServiceV3.orderItem()
| |-->OrderRepositoryV3.save()
...
```

### 주의사항 및 부가 로그 발생 원인

* 실제 실행 시 위 요청 외에도 다음과 같은 로그가 출력됨:

  * `AppV1Config`, `AppV2Config` 등 수동 빈 설정 클래스 내부 메서드 호출 로그
  * `WebMvcConfigurationSupport` 등 내부 스프링 설정 빈 생성 로그
  * `CglibAopProxy` 경고: `final` 키워드가 붙은 메서드는 프록시 대상에서 제외됨

* 원인:

  * `NameMatchMethodPointcut`은 클래스 범위 제한이 없기 때문에 Spring 내부 클래스에 포함된 `"request*"`, `"order*"`, `"save*"` 메서드도 프록시 대상으로 감지
  * 따라서 내부 Bean 초기화 과정에서도 AOP가 적용되어 로그 출력됨

* 해결 방향:

  * 필요 시 `pointcut.setClassFilter()`로 특정 패키지 이하의 클래스만 프록시 대상으로 제한 가능
  * 또는 Aspect 방식의 AOP로 전환해 `@Around` 등으로 명확히 범위 지정 가능

---
# 250522
## @Aspect AOP 
### @Aspect 애노테이션을 활용한 AOP 설정

* Spring Boot에서 AOP를 편리하게 적용하기 위해 `@Aspect` 애노테이션을 활용할 수 있다.
* 이를 통해 별도의 `Advisor` 설정 없이 자동으로 프록시가 생성된다.
* AspectJ에 대한 자세한 내용은 추후 학습하고, 우선 프록시 생성이 자동으로 이루어지는 편리함에 집중한다.

### LogTraceAspect 클래스 생성

* 위치: `src > … > proxy > config > v6_aop > aspect`
* `@Aspect`, `@Component`, `@Slf4j` 애노테이션 설정
* `LogTrace`를 `final`로 필드에 선언

#### `@Around` 애노테이션 설정

* 포인트컷: `"execution(* hello.proxy.app..*(..))"`

  * `hello.proxy.app` 하위의 모든 메서드에 AOP 적용

#### `execute` 메서드 구현

* 반환 타입: `Object`
* 매개변수: `ProceedingJoinPoint`
* 예외 처리: `throws Throwable`



### AopConfig 클래스 생성

* 위치: `src > … > proxy > config > v6_aop`
* `@Configuration` 애노테이션 설정
* `@Import(AppV1Config.class)`, `@Import(AppV2Config.class)`로 기존 수동 등록된 Bean 포함

#### LogTraceAspect 빈 등록

* `@Bean` 메서드로 `LogTraceAspect` 인스턴스 반환
* 생성자 파라미터로 `LogTrace` 주입

### Main 클래스에 설정 등록

* `@Import(AopConfig.class)`를 통해 설정 적용

### 실행 결과 로그

* 프록시가 자동으로 적용되어 다음과 같이 메서드 호출 트레이싱이 이루어진다:

```
OrderControllerV1Impl.request(..)
|-->OrderServiceV1Impl.orderItem(..)
| |-->OrderRepositoryV1Impl.save(..)
| |<--OrderRepositoryV1Impl.save(..) time = 1003ms
|<--OrderServiceV1Impl.orderItem(..) time = 1005ms
OrderControllerV1Impl.request(..) time = 1008ms
```




