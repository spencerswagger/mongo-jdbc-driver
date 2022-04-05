# MongoDb JDBC Driver

这是一个基于[JetBrains](https://www.jetbrains.com/) 公司开发的IDE
[DataGrip](https://www.jetbrains.com/datagrip/) 中的
[MongoDB Java Driver](https://github.com/DataGrip/mongo-jdbc-driver) 衍生的开源JDBC驱动。

本仓库包含以下内容：

1. 将该驱动应用到自己的项目中
2. 添加、完善一些特性，以让其更适合开发者使用(_待完成_)

## 内容列表

- [背景](#背景)
- [使用说明](#使用说明)
    - [下载](#下载)
    - [自行构建](#自行构建)
      - [Maven](#Maven)
      - [Gradle](#Gradle)
- [相关仓库](#相关仓库)
- [维护者](#维护者)
- [如何贡献](#如何贡献)
- [使用许可](#使用许可)

## 背景

最开始因为部分为JDBC开发的功能，由于各种原因将原关系型数据库迁移到了MongoDB中，导致大量JDBC的代码无法使用（即使重写SQL）。
所以经过一段时间寻找，[DataGrip](https://www.jetbrains.com/datagrip/) 中的
[MongoDB Java Driver](https://github.com/DataGrip/mongo-jdbc-driver)
是经过产品反复验证且完善的开源驱动，所以决定使用它。

但我们在使用过程中发现，它的功能并不完善，比如：

- PreparedStatement 的并不支持占位符替换变量查询

所以我们决定将其进行完善，做一些简单的改造，以让其更像是一个JDBC驱动。

> 当然，如果你有其他的建议，欢迎提出Issue。

## 使用说明

原项目[MongoDB Java Driver](https://github.com/DataGrip/mongo-jdbc-driver)
使用2位数字作为版本号，我们将会在其后面添加一位数字，以表明是基于哪个原版本的衍生版。

> 该项目并未发布到Maven仓库，所以需要自行构建或下载。

> 该项目目前仅作为开发学习使用，不确保稳定性及安全性。若对这两点有要求，请使用原版
> [MongoDB Java Driver](https://github.com/DataGrip/mongo-jdbc-driver)

### 下载

请到 [Release](https://github.com/spencerswagger/mongo-jdbc-driver/releases) 下载并安装到本地或私有仓库。

### 自行构建

这个项目使用 [Gradle 7+](https://gradle.org/) 构建。请确保你本地安装了它们。

构建项目：

```bash
./gradlew shadowJar
```

在构建完成后，会生成一个jar包，可以直接使用。

你也可以发布到私有仓库：

- 先在init.gradle中添加你的私有仓库信息：

```groovy
allprojects {
    apply plugin: 'maven-publish'
    publishing {
        repositories {
            maven {
                url = maven_repo
                credentials {
                    username maven_username
                    password maven_password
                }
            }
        }
    }
}
```

> 请不要忘记替换`maven_repo`、`maven_username`、`maven_password`

- 然后发布到私有仓库

你也可以改动`groupId`、`artifactId`以便于你使用，但不建议改动版本号，以免后续更新问题。

```bash
./gradlew publishShadowPublicationToMavenRepository
```

#### Maven

```xml
<dependency>
    <groupId>com.github.spencerswagger</groupId>
    <artifactId>mongo-jdbc-driver</artifactId>
    <version>1.14.0</version>
</dependency>
```

#### Gradle

```groovy
dependencies {
    compile 'com.github.spencerswagger:mongo-jdbc-driver:1.14.0'
}
```

## 相关仓库

- [MongoDB Java Driver](https://github.com/DataGrip/mongo-jdbc-driver) — [JetBrains](https://www.jetbrains.com/)
  公司开发的IDE
  [DataGrip](https://www.jetbrains.com/datagrip/) 中的 MongoDB Java Driver

## 维护者

[@SpencerSwagger](https://github.com/spencerswagger)

## 如何贡献

非常欢迎你的加入！[提一个 Issue](https://github.com/spencerswagger/mongo-jdbc-driver/issues/new) 或者提交一个 Pull Request。

该项目遵循 [Contributor Covenant](http://contributor-covenant.org/version/1/3/0/) 行为规范。

### 贡献者

感谢原作者 [@kornilova203](https://github.com/kornilova203)

## 使用许可

[Apache License 2.0](license.txt)