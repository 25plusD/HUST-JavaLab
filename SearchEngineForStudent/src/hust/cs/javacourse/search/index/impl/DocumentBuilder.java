package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.impl.LengthTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.PatternTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.StopWordTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.TermTupleScanner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//构建文档的Document，主要是build方法，既可以通过传入文档id、文档绝对路径和文档对应的TermTupleStream来构建，也可以通过传入文档id、文档绝对路径和文档对应的文档对应File对象来构建。
//实际使用的时候，会通过调用第二个build函数进而调用第一个build函数，从而完成整个build过程。在第二个build函数中需要调用三种Filter对得到的三元组进行过滤。
public class DocumentBuilder extends AbstractDocumentBuilder {

    @Override
    public AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream) {
        List<AbstractTermTuple> list = new ArrayList<>();
        AbstractTermTuple temp;
        while((temp = termTupleStream.next()) != null)
            list.add(temp);
        termTupleStream.close();
        return new Document(docId, docPath, list);
    }

    @Override
    public AbstractDocument build(int docId, String docPath, File file) {
        BufferedReader reader;
        AbstractTermTupleStream scanner = null;
        try{
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            scanner = new TermTupleScanner(reader);
            //scanner = new StopWordTermTupleFilter(scanner);
            scanner = new LengthTermTupleFilter(new PatternTermTupleFilter(new StopWordTermTupleFilter(scanner)));
            //scanner = new PatternTermTupleFilter(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        assert scanner != null;
        return build(docId,docPath,scanner);
    }
}