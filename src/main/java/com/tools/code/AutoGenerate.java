package com.tools.code;

import java.io.File;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeInstance;

import com.tools.io.FileTool;
import com.tools.io.StreamTool;

public class AutoGenerate {
	private static final RuntimeInstance RUNTIME_INSTANCE = new RuntimeInstance();
	private static final String UTF_8 = "UTF-8";
	static {
		Properties properties = new Properties();
		properties.setProperty(Velocity.INPUT_ENCODING, UTF_8);
		properties.setProperty(Velocity.OUTPUT_ENCODING, UTF_8);
		properties.setProperty("file.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		RUNTIME_INSTANCE.init(properties);
	}

	private AutoGenerate() {

	}

	public static void generate(Type[] types) {
		for (Type type : types) {
			if (FileTool.mkdir(type.path())) {
				Writer writer = StreamTool.toWriter(new File(type.name()));
				VelocityContext context = new VelocityContext();
				context.put("model", type.model());
				Template template = RUNTIME_INSTANCE.getTemplate(type.tpl(), UTF_8);
				template.merge(context, writer);
				StreamTool.close(writer);
			}
		}
	}

}
