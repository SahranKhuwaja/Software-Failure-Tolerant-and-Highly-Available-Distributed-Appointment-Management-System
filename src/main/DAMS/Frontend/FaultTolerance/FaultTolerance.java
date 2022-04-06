package DAMS.Frontend.FaultTolerance;

import DAMS.Response.Response;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FaultTolerance {

    List<Response> responseQueue;

    public FaultTolerance(List<Response> responseQueue){
        this.responseQueue = responseQueue;
    }

    public static void main(String[] args){
        Response rep1 = new Response("AddAppointment","AddAppointment"
                , true, "Success");
        rep1.setReplica(1);
        Response rep2 = new Response("AddAppointment","AddAppointment"
                , false, "Success");
        rep2.setReplica(2);
        Response rep3 = new Response("AddAppointment","AddAppointment"
                , true, "Success");
        rep3.setReplica(3);
        Response rep4 = new Response("AddAppointment","AddAppointment"
                , true, "Success");
        rep4.setReplica(4);
        List<Response> r = Arrays.asList(
                rep1, rep2, rep3, rep4
        );
        FaultTolerance faultTolerance = new FaultTolerance(r);
        System.out.println(faultTolerance.detectSoftwareFailure().getReplica());
    }

    public Response detectSoftwareFailure(){
        AtomicInteger index = new AtomicInteger(1);
        LinkedHashMap<Integer, List<Boolean>> replicas = new LinkedHashMap<Integer, List<Boolean>>();
        List<Integer> failedReplicas = null;
        Response replicaResponse = responseQueue.get(0);;
        if(responseQueue.size()==1){
            return replicaResponse;
        }
        responseQueue.stream()
                .forEach(e->{
                    List<Boolean> defectedReplicas = responseQueue.stream()
                            //.filter(el->!el.equals(e))
                            .map(el->this.compare(e,el))
                            .collect(Collectors.toList());
                    replicas.put(index.get(),defectedReplicas);
                    index.set(index.get()+1);
                });
        System.out.println(replicas.toString());
        failedReplicas = this.softwareFailedReplicas(replicas);
        System.out.println(failedReplicas);
        if(failedReplicas.size()==4){
            return replicaResponse;
        }
        if(!failedReplicas.contains(responseQueue.get(0).getReplica())){
            replicaResponse = responseQueue.get(0);
        }
        else if(!failedReplicas.contains(responseQueue.get(1).getReplica())){
            replicaResponse = responseQueue.get(1);
        }
        else if(!failedReplicas.contains(responseQueue.get(2).getReplica())){
            replicaResponse = responseQueue.get(2);
        }
        else if(!failedReplicas.contains(responseQueue.get(3).getReplica())){
            replicaResponse = responseQueue.get(3);
        }
        return replicaResponse;
    }

    private boolean compare(Response r1, Response r2){
        boolean response = false;

        switch (r1.methodName()){
            case "GetAppointmentTypes":
            case "GetTimeSlots":
              response = r1.success()==r2.success() && r1.getMessages().equals(r2.getMessages());
              break;
            case "AddAppointment":
            case "RemoveAppointment":
            case "ListAppointmentAvailability":
            case "BookAppointment":
            case "CancelAppointment":
            case "SwapAppointment":
            case "GetAppointmentSchedule":
                response = r1.success()==r2.success();
                break;
            case "ViewAppointment":
                response = r1.success()==r2.success() && r1.getResponseWrapper().getData().equals(r2.getResponseWrapper().getData());
                break;
        }
        return response;
    }

    private List<Integer> softwareFailedReplicas(LinkedHashMap<Integer,List<Boolean>> replicas){
        List<Integer> detectedFailures = new ArrayList<Integer>();
        Map<Object, Long> count = replicas.values()
                .stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        replicas.entrySet()
                .stream()
                .forEach(e->{
                    if(count.get(e.getValue())<=2 && replicas.size()==4){
                        detectedFailures.add(e.getKey());
                    }
                    if(count.get(e.getValue())<=1 && (replicas.size()==2 || replicas.size()==3)){
                        detectedFailures.add(e.getKey());
                    }
                });
        return detectedFailures;
    }



}
