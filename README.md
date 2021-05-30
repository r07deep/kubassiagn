In The Kmeans-impl the following has been implemented:
1.	Capturing the http://localhost:9090/api/node/groups request of user in PodController.java.
2.	PodController calls the PodController.java, which reads the data from filesystem. NOTE: Having issue with http://localhost:8080/v1/api/pods in code ,so read from filesystem.

3.	Then further processed the RAW data with MapReduce.java and generated the following. Output of MapReduce.java Intermediate data-> Node distribution with AppGroups {n1=[a, b, c], n5=[a, b, c], n3=[d, e, f, g], n2=[d, e, g], n6=[d, e, f], n4=[f, g]}

4.	Now Applied the K-Means Clustering Algo and did the following: 

i > Converted the above apps to its ASCII Value like following and wrote to Sample.txt file. {n1=[97, 98, 99], n5=[97, 98, 99], n3=[100, 101, 102, 103], n2=[100, 101, 103], n6=[100, 101, 102], n4=[102, 103]} This values are written to Sample.txt.

ii > Then called the K_Cluster.Java, where it takes the above node and ASCII value mapping of the app. //call to K_Cluster. // Path of Sample.txt,Totalrows of Sample.txt and cluster size of 2. kCluster.getCluster(outputFilePath, lineCount, 2);

iii> K_Cluster genarates the following file with the Cluster mapping with Apps(AppGroup.txt) like following iv> At this stage I have two txt Files Sample.txt and AppGroup.txt.

Sample.txt (In the Sample.txt the nos are ASCII values of the Apps. )
________________________________________
97,n1
98,n1
99,n1
97,n5
98,n5
99,n5
100,n3
101,n3
102,n3
103,n3
100,n2
101,n2
103,n2
100,n6
101,n6
102,n6
102,n4
103,n4

AppGroup.txt (Here in the below file 0 =>G1, which has a,b,c and 1=>G2, which has rest of the apps. )
________________________________________
g,1 
b,0 
d,1 
f,1 
c,0 
d,1 
e,1 
c,0 
a,0 
e,1 
g,1
d,1
f,1
g,1 
f,1 
e,1 
b,0 
a,0
v> Now facing little challenge in putting the contents of Sample.txt and AppGroup.txt to following:
g1: { nodes: [n1, n5], app: [a, b, c] }, g2: { nodes: [ n2, n3, n4, n6 ], app: [d, e, f, g] } }

