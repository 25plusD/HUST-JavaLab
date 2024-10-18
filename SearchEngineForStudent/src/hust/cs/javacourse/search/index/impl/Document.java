package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractTermTuple;

import java.util.List;

//保存每一个文档的基本信息，包括文档所在路径docPath，文档唯一编号docId，同时还有组成整个文档的所有单词所构成的TermTuple的List。
//addTuples方法：将传入的Tuple添加到三元组中，contains方法判断三元组中是否存在传入的Tuple。
//getTuple方法：根据传入的下标获取Tuple。
public class Document extends AbstractDocument {

    public Document() {
    }

    public Document(int docId, String docPath) {
        super(docId, docPath);
    }

    public Document(int docId, String docPath, List<AbstractTermTuple> tuples) {
        super(docId, docPath, tuples);
    }

    @Override
    public int getDocId() {
        return this.docId;
    }

    @Override
    public void setDocId(int docId) {
        this.docId = docId;
    }

    @Override
    public String getDocPath() {
        return this.docPath;
    }

    @Override
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    @Override
    public List<AbstractTermTuple> getTuples() {
        return this.tuples;
    }

    @Override
    public void addTuple(AbstractTermTuple tuple) {
        if(!this.tuples.contains(tuple))
            this.tuples.add(tuple);
    }

    //document contains tuple using tuples contains tuple to solve
    @Override
    public boolean contains(AbstractTermTuple tuple) {
        return this.tuples.contains(tuple);
    }

    //获取索引为index的tuple
    @Override
    public AbstractTermTuple getTuple(int index) {
        return this.tuples.get(index);
    }

    @Override
    public int getTupleSize() {
        return this.tuples.size();
    }

    @Override
    public String toString() {
        return "docId: " + this.docId + ", " + "docPath: " + this.docPath + "tuples: " + this.tuples;
    }

}