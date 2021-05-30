# kubassiagn
In The Kmeans-impl I did the follwoing: 

1. Capturing the http://localhost:9090/api/node/groups request of user in PodController.java.
2. PodController calls the PodController.java, which reads the data from filesystem. 
NOTE: Having issue with http://localhost:8080/v1/api/pods in code ,so read from filesystem.

3. Then further processed the RAW data with MapReduce.java and genareted the following. 
Output of MapReduce.java
Intermidaite data-> Node distribution with AppGroups
{n1=[a, b, c], n5=[a, b, c], n3=[d, e, f, g], n2=[d, e, g], n6=[d, e, f], n4=[f, g]}

4. Now Applied the K-Means Clustering Algo and did the following: 
 i > Converted the above apps to its ASCII Value like following and wrote to Sample.txt file.
 {n1=[97, 98, 99], n5=[97, 98, 99], n3=[100, 101, 102, 103], n2=[100, 101, 103], n6=[100, 101, 102], n4=[102, 103]}
 This values are written to Sample.txt.
 
ii > Then called the K_Cluster.Java, where it takes the above node and ASCII value mapping of the app.
    //call to K_Cluster.
    // Path of Sample.txt,Totalrows of Sample.txt and cluster size of 2.
    kCluster.getCluster(outputFilePath, lineCount, 2);
    
iii> K_Cluster genarates the following file with the Cluster mapping with Apps(AppGroup.txt) like following
iv> At this stage I have two txt Files Sample.txt and AppGroup.txt. 
Here in the below file  0 =>G1, which has a,b,c and 1=>G2, which has rest of the apps. 

Sample.txt                                    AppGroup.txt
---------                                     ------------
---------                                     ------------      
97,n1                                           g,1
98,n1                                           b,0
99,n1                                           d,1
97,n5                                           f,1
98,n5                                           c,0
99,n5                                           d,1
100,n3                                          e,1
101,n3                                          c,0
102,n3                                          a,0
103,n3                                          e,1
100,n2                                          g,1
101,n2                                          d,1
103,n2                                          f,1
100,n6                                          g,1
101,n6                                          f,1
102,n6                                          e,1
102,n4                                          b,0
103,n4                                          a,0

In the Sample.txt the nos are ASCII values of the Apps. 

v> Now facing little challenge in putting the contents of Sample.txt and AppGroup.txt to following:
g1: {
nodes: [n1, n5],
app: [a, b, c]
},
g2: {
nodes: [ n2, n3, n4, n6 ],
app: [d, e, f, g]
}
}

                                                        
                                                          
                                                         
                                                         
                                                         
                                                          
                                                         
                                                         
                                                         
                                                          
                                                         
                                                          
                                                         
                                                         
                                                          
                                                          
                                                          
                                               
                                                          

  
 
