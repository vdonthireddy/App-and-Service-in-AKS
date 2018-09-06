cd ServiceInAKS
mvn clean install
docker rmi -f serviceinaks
docker build -t serviceinaks .
docker tag serviceinaks vdonthireddy/serviceinaks:8.0
docker push vdonthireddy/serviceinaks:8.0
cd ../AppInAKS
npm install 
docker rmi -f appinaks
docker build -t appinaks .
docker tag appinaks vdonthireddy/appinaks:8.0
docker push vdonthireddy/appinaks:8.0
cd ..
kubectl delete deploy vjservice-deployment & kubectl delete service vjservice-service
kubectl delete deploy vjapp-deployment & kubectl delete service vjapp-service
kubectl apply -f aks.yml
kubectl get po,deploy,svc