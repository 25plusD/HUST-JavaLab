package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//根据构建好的索引搜索词条，数据成员index用于保存要用的索引。
//open方法：将索引文件实例加载到index中。
//search方法：有两个，分别实现对一个词和对两个词的检索。
//对于一个词的检索，直接使用index的search方法，利用返回的postinglist构建Hit数组，对Hit数组排序后返回最终结果。
//对于两个词的搜索，对两个词分别进行搜索后，针对结合规则，对搜索结果进行合并或者筛选出重合的，利用SimpleSorter排序后返回Hit数组。
public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index.load(new File(indexFile));
        this.index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList postList=index.search(queryTerm);
        if(postList==null) return new AbstractHit[0];
        List<AbstractHit> hitList= new ArrayList<>();
        for(int i=0;i<postList.size();i++){
            AbstractPosting post=postList.get(i);
            Map<AbstractTerm,AbstractPosting> map= new HashMap<>();
            map.put(queryTerm,post);
            AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
            hit.setScore(sorter.score(hit));
            hitList.add(hit);
        }
        sorter.sort(hitList);
        return hitList.toArray(new AbstractHit[0]);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList postingList1=index.search(queryTerm1);
        AbstractPostingList postingList2=index.search(queryTerm2);
        List<AbstractHit> hitList=new ArrayList<>();

        //任意一个检索词出现在命中文档
        if(combine==LogicalCombination.OR){
            int i=0,j=0;
            while(i<postingList1.size()&&j<postingList2.size()){
                AbstractPosting posting1=postingList1.get(i);
                AbstractPosting posting2=postingList2.get(j);
                AbstractPosting post;
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();

                //当两个文档相同时，将两个检索词和对应文档都加入到命中文档
                if(posting1.getDocId()==posting2.getDocId()){
                    post=posting1;
                    map.put(queryTerm1,posting1);
                    map.put(queryTerm2,posting2);
                    i++;
                    j++;
                }

                //将文档ID小的那个先加入
                else if(posting1.getDocId()<posting2.getDocId()){
                    post=posting1;
                    map.put(queryTerm1,posting1);
                    i++;
                }
                else{
                    post=posting2;
                    map.put(queryTerm2,posting2);
                    j++;
                }
                AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
                hit.setScore(sorter.score(hit));
                hitList.add(hit);
            }

            //将余下的文档加入到命中文档
            while(i<postingList1.size()){
                AbstractPosting post=postingList1.get(i);
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                map.put(queryTerm1,post);
                AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
                hit.setScore(sorter.score(hit));
                hitList.add(hit);
            }
        }

        //两个检索词都必须同时出现在文档中
        else{
            int i=0,j=0;
            while(i<postingList1.size()&&j<postingList2.size()){
                AbstractPosting posting1=postingList1.get(i);
                AbstractPosting posting2=postingList2.get(j);
                if(posting1.getDocId()==posting2.getDocId()){
                    Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                    map.put(queryTerm1,posting1);
                    map.put(queryTerm2,posting2);
                    AbstractHit hit=new Hit(posting1.getDocId(),index.getDocName(posting1.getDocId()),map);
                    hit.setScore(sorter.score(hit));
                    hitList.add(hit);
                    i++;
                    j++;
                }
                else if(posting1.getDocId()<posting2.getDocId()) i++;
                else j++;
            }
        }
        sorter.sort(hitList);
        return hitList.toArray(new AbstractHit[0]);
    }
}
