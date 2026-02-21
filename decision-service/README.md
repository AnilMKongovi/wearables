Rules Engine / Decision Service — HIGH VALUE

This is what makes your platform smart.

Purpose
	Convert events → actions.

Example:
	IF heart_rate > 120 for 2 min
	THEN send alert push


decision-service (port 8085)
Kafka consumer on device.events → evaluates RuleEngine → publishes AlertEvent to alert.events
Rules: heart rate >120 bpm → HIGH alert, <45 bpm → LOW alert; any FALL_DETECTED; battery ≤15%
Pure event-driven; no HTTP endpoints (only actuator)
