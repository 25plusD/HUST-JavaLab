package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

//实现索引，最主要的两个个数据成员：以map实现文档索引的docIdToDocPathMapping和实现关键词索引的termToPostingListMapping。
//实现了addDocument方法：对传入的Document，将docId和docPath加入docIdToDocPathMapping索引，同时遍历该文档的所有三元组，更新termToPostingListMapping成员。
//load和save方法：实际山就是将File类型的参数转化为Object的流类型，从而调用自身的writeObject/readObject方法。
//search方法：传入Term返回对应的postingList（如果不存在返回null）。
//optimize方法：对索引进行优化，即将每一个单词的PostingList按照docId进行排序，同时对每一个Posting里面的position排序。
//getDocName方法：根据传入的docId获取完全路径名。
//writeObject和readObject方法：对所建立的索引进行序列化和反序列化操作。
/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        if(docIdToDocPathMapping.size() == 0 && termToPostingListMapping.size() == 0)
            return null;
        else
            return "docIdToDocPathMap:\n" + docIdToDocPathMapping.toString() + "\ntermTOPosingListMap:\n" + termToPostingListMapping.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {

        //获取文档里三元组的映射关系，term -> PostingList
        //加入term，加入和自增pos
        HashMap<AbstractTerm,List<Integer>> map = new HashMap<>();
        for(AbstractTermTuple termTuple : document.getTuples()){
            if(!map.containsKey(termTuple.term)) {
                map.put(termTuple.term, new ArrayList<>());
                map.get(termTuple.term).add(termTuple.curPos);
            }
            else
                map.get(termTuple.term).add(termTuple.curPos);
        }

        //更新索引
        //term->(docId , freq , position)
        for(Map.Entry<AbstractTerm,List<Integer>> entry : map.entrySet()){
            if(!this.termToPostingListMapping.containsKey(entry.getKey()))
                termToPostingListMapping.put(entry.getKey(),new PostingList());
            termToPostingListMapping.get(entry.getKey()).add(new Posting(document.getDocId(),entry.getValue().size(),entry.getValue()));
        }
        //docId -> docPath
        docIdToDocPathMapping.put(document.getDocId(),document.getDocPath());
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try{
            //创建一个ObjectInputStream输入流；
            //调用ObjectInputStream对象的readObject()得到序列化的对象。
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            readObject(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try{
            //创建一个ObjectOutputStream输出流；
            //调用ObjectOutputStream对象的writeObject输出可序列化对象。
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            writeObject(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.getOrDefault(term, null);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for(Map.Entry<AbstractTerm,AbstractPostingList> entry : termToPostingListMapping.entrySet()){
            entry.getValue().sort();
            for(int i = 0 ; i < entry.getValue().size(); ++i)
                Collections.sort(entry.getValue().get(i).getPositions());
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try{
            out.writeObject(docIdToDocPathMapping);
            out.writeObject(termToPostingListMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readObject(ObjectInputStream in) {
        try{
            docIdToDocPathMapping = (Map<Integer, String>) in.readObject();
            termToPostingListMapping = (Map<AbstractTerm, AbstractPostingList>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}