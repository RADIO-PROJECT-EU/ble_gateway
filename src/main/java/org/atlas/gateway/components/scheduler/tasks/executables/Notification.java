package org.atlas.gateway.components.scheduler.tasks.executables;

import org.atlas.gateway.components.scheduler.tasks.TaskExecutable;

public class Notification implements TaskExecutable{

	@Override
	public void execute() {
		System.out.println("Sending notifications ahahhahaaha");
	}

}
