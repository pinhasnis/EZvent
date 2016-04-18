package utils.Event_Helper_Package;

import java.util.HashMap;

import utils.Constans.Constants;

/**
 * Created by Ravid on 08/02/2016.
 */
public class Task_Helper {
    private String type;//Personal / Group. subTask is task with subTask_id>0 (subTask<Integer, String> - Integer > 0)
    private String description;
    private String User_ID;
    private String mark;//For self marking tasks.
    private HashMap<Integer, String[]> subTasks;//string[0]-description, string[1]-mark.
    private int subTask_ID_generator;//Store the last ID number that was in use.

    public Task_Helper(String type, String description) {
        this.type = type;
        this.description = description;
        this.User_ID = Constants.UnCheck;
        this.mark = Constants.No;
        this.subTasks = new HashMap<>();
        subTask_ID_generator = 0;
    }

    public Task_Helper(String type, String description, String User_ID, String mark) {
        this.type = type;
        this.description = description;
        this.User_ID = User_ID;
        this.mark = mark;
        this.subTasks = new HashMap<>();
        subTask_ID_generator = 0;
    }

    public Task_Helper(String type, String description, String User_ID, String mark, Object subTasks, int subTask_id_generator) {
        this.type = type;
        this.description = description;
        this.User_ID = User_ID;
        this.mark = mark;
        this.subTasks = (HashMap<Integer, String[]>) subTasks;
        subTask_ID_generator = subTask_id_generator;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public HashMap<Integer, String[]> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, String[]> subTasks) {
        this.subTasks = subTasks;
    }

    public int getSubTask_ID_generator() {
        return subTask_ID_generator;
    }

    public void setSubTask_ID_generator(int subTask_ID_generator) {
        this.subTask_ID_generator = subTask_ID_generator;
    }

    public Task_Helper make_copy() {
        return new Task_Helper(type, description, User_ID, mark, subTasks.clone(), subTask_ID_generator);
    }
}
