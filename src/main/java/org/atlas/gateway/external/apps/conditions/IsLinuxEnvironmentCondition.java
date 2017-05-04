package org.atlas.gateway.external.apps.conditions;

import org.atlas.gateway.utils.OsUtils;
import org.atlas.gateway.utils.OsUtils.OSType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class IsLinuxEnvironmentCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return (OsUtils.getOperatingSystemType()==OSType.Linux);
	}

}
