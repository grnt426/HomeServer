package app;

import app.ac.AcController;
import app.ambiance.AmbientHttpControllerFacade;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.lights.LightController;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;

import java.io.IOException;

@Configuration
@ComponentScan({"app"})
public class Application {

	public static void main(String[] args) throws ClassNotFoundException, IOException, MqttException {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class);
		new WebConfig(ctx.getBean(AcController.class), ctx.getBean(DeviceHttpControllerFacade.class),
				ctx.getBean(HeartbeatController.class), ctx.getBean(LightController.class),
				ctx.getBean(AmbientHttpControllerFacade.class));
		ctx.registerShutdownHook();
	}

	@Bean
	public Sql2o dataSource() throws ClassNotFoundException {
		Sql2o sql2o;
		Class.forName("org.sqlite.JDBC");
		sql2o = new Sql2o("jdbc:sqlite:database.db", null, null);
		return sql2o;
	}
}
