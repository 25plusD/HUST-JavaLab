## **基于内存的搜索引擎设计和实现**

**1.** **文件清单及其功能说明**

Experiment1Test(JDK17)是自动测试文件，其中所有的.class文件已经放入betest目录下。

SearchEngineForStudent是实验的源码工程，其中bin目录下包括.class文件，javadoc目录下包含javadoc文件，src目录下包括,java文件，text目录下包括测试集文件。

CS2205_U202215510_徐新飏.doc文件为倒排索引的实验报告。

**2.** **用户使用说明书**

自动测试：打开Experiment1Test(JDK17)，在betest目录下放入项目的.class文件（这里已提前放入），在Windows环境下，在终端运行test.bat脚本文件；在Linux环境下，运行test.sh，运行后在test-output中的index.html可以得到自动测试的结果。

PS：运行时注意自动测试包不要在中文目录下

手动测试：可以将要检索的文档加到项目工程SearchEngineForStudent的text目录下，先后对hust.cs.javacourse.search.run包下的TestBuildIndex.java和TestSearchIndex.java编译运行。

**3.** **源代码**

见附件中的SearchEngineForStudent\src文件。