package com.udemy.spark.app;

import com.google.common.collect.Iterators;
import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import scala.Tuple2;

import java.util.Iterator;

import static com.mongodb.spark.MongoSpark.save;

public class App {
    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir", "D:\\hadoop-common-2.2.0-bin-master");

        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("MongodbSpark")
                .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/test1.WorldCupCollection")
                .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/test1.WorldCupCollection")
                .getOrCreate();

        JavaSparkContext cont = new JavaSparkContext(spark.sparkContext());

        JavaRDD<String> Raw_Data = cont.textFile("C:\\Users\\TEMP\\Desktop\\WorldCupPlayers.csv");

        JavaRDD<PlayersModel> playersRDD = Raw_Data.map(new Function<String, PlayersModel>() {
            public PlayersModel call(String line) throws Exception {
                String[] dizi = line.split(",", -1);
                return new PlayersModel(dizi[0], dizi[1], dizi[2], dizi[3], dizi[4], dizi[5], dizi[6], dizi[7], dizi[8]);

            }
        });
        JavaRDD<PlayersModel> tur = playersRDD.filter(new Function<PlayersModel, Boolean>() {
            public Boolean call(PlayersModel playersModel) throws Exception {
                return playersModel.getTeam().equals("TUR");
            }
        });


        JavaPairRDD<String, String> mapRDD = tur.mapToPair(new PairFunction<PlayersModel, String, String>() {
            public Tuple2<String, String> call(PlayersModel playersModel) throws Exception {
                return new Tuple2<String, String>(playersModel.getPlayerName(), playersModel.getMatchID());
            }
        });

        JavaPairRDD<String, Iterable<String>> groupPlayers = mapRDD.groupByKey();


        JavaRDD<groupPlayer> resultRDD = groupPlayers.map(new Function<Tuple2<String, Iterable<String>>, groupPlayer>() {
            public groupPlayer call(Tuple2<String, Iterable<String>> dizi) throws Exception {
                Iterator<String> iteratorraw = dizi._2.iterator();
                int size = Iterators.size(iteratorraw);
                return new groupPlayer(dizi._1, size);
            }
        });



        JavaRDD<Document> map = resultRDD.map(new Function<groupPlayer, Document>() {

            public Document call(groupPlayer groupPlayer) throws Exception {

                return Document.parse("{PlayerName: " + " ' " + groupPlayer.getPlayerName() + "'"
                        + ","+"PlayerMatchCount: "+ groupPlayer.getMatchCount()
                        +"}");
            }
        });

        MongoSpark.save(map);

    }
}


