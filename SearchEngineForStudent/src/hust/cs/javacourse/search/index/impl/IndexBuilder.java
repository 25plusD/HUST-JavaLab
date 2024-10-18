package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.*;

//构建整个倒排索引，在这个类中利用上述结构和类，对于传入的目录进行操作最终得到整个倒排索引结构。
// 比较重要的数据成员是用于构建每个文档的docBuilder和用于给文档赋予Id号的docId。
// 这个类的主要方法buildIndex接收一个表示根目录的字符串,通过遍历该目录下的文本文件，生成Document并逐步加入Index中，最终进行返回，需要注意目录为空的情况。
public class IndexBuilder extends AbstractIndexBuilder {

    private int docNum = 0;

    public IndexBuilder(AbstractDocumentBuilder docBuilder) {
        super(docBuilder);
    }

    @Override
    public AbstractIndex buildIndex(String rootDirectory) {

        //DocumentBuilder documentBuilder = new DocumentBuilder();
        AbstractIndex index = new Index();
        for(String path : FileUtil.list(rootDirectory)){
            AbstractDocument document = docBuilder.build(docNum++,path,new File(path));
            index.addDocument(document);
        }
        return index;
    }

}