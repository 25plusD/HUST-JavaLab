package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//本质上是对String类型的简单包装
public class Term extends AbstractTerm {


    public Term() {
    }


    public Term(String content) {
        super(content);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        else if(obj instanceof Term){
            Term term = (Term)obj;
            if(term.content != null && this.content != null)
                return term.content.equals(this.content);
            else return term.content == null && this.content == null;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.content;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(AbstractTerm o) {
        return content.compareTo(o.getContent());
    }
    //对Term类型的对象,转化为用String类的compareTo比较

    //从接口继承的方法 hust.cs.javacourse.search.index.FileSerializable
    @Override
    public void writeObject(ObjectOutputStream out) {
        try{
            //将对象写入输出流
            out.writeObject(this.content);
        } catch (IOException e) {
            // 捕获异常并打印异常堆栈信息
            e.printStackTrace();
        }
    }

    @Override
    public void readObject(ObjectInputStream in) {
        try{
            this.content = (String)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}