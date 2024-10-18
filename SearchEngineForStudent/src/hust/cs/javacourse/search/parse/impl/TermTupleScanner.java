package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//主要数据成员：pos用于统计单词位置，inputBuffer用于存放临时读到的内容。
//由于读取文档的时候是一行一行读取，但是取出来的时候是一个单词一个单词的取出来，所以就需要临时存放多余的内容。
//当调用next方法的时候，需要进行判断，如果inputBuffer不为空，则直接从inputBuffer中获取数据。
//如果inputBuffer为空则再次从input流中读取一行，其间需要注意更新pos字段，需要注意的是需要根据config中的选项来判断是否需要转化大小写。
//三个具体的Filer都是通过对AbstractTermTupleStream不断调用next方法来获取单词，进而将符合要求的返回，不符合要求的则直接丢弃。
public class TermTupleScanner extends AbstractTermTupleScanner {

    private int position = 0;  //记录单词的全局位置
    private List<String> buf;
    private StringSplitter stringSplitter = new StringSplitter();

    /**
     * 缺省构造函数
     */
    public TermTupleScanner() {
        buf = new ArrayList<>();
        stringSplitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
    }

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        try{
            if(buf.isEmpty()){
                String s;
                StringBuilder sb = new StringBuilder();
                while( (s = input.readLine()) != null){
                    sb.append(s).append("\n"); //reader.readLine())返回的字符串会去掉换行符，因此这里要加上
                }
                s = sb.toString().trim();
                s = s.toLowerCase();
                buf = stringSplitter.splitByRegex(s);
            }
            if(buf.size() == 0)
                return null;
            AbstractTerm term = new Term(buf.get(0));
            buf.remove(0);
            return new TermTuple(term,position++);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 实现父类AbstractTermTupleStream的close方法，关闭流
     */
    @Override
    public void close() {
        super.close();
    }

    /**
     * 构造函数
     *
     * @param input ：指定输入流对象，应该关联到一个文本文件
     */
    public TermTupleScanner(BufferedReader input) {
        super(input);
        buf = new ArrayList<>();
        stringSplitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
    }
}
