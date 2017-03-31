package net.kodehawa.mantarobot.core.listeners.operations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.kodehawa.mantarobot.core.listeners.OptimizedListener;
import net.kodehawa.mantarobot.utils.Expirator;
import net.kodehawa.mantarobot.utils.Expirator.Expirable;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

public class InteractiveOperations {
	@Getter @RequiredArgsConstructor
	private static class RunningOperation implements Expirable {
		private final OptionalInt increasingTimeout;
		private final InteractiveOperation operation;
		private final String operationName;

		@Override
		public void onExpire() {
			OPERATIONS.values().remove(this);
		}
	}

	private static final Expirator<RunningOperation> EXPIRATOR = new Expirator<>();
	private static final Map<String, RunningOperation> OPERATIONS = new ConcurrentHashMap<>();
	private static final EventListener LISTENER = new OptimizedListener<GuildMessageReceivedEvent>(GuildMessageReceivedEvent.class) {
		@Override
		public void event(GuildMessageReceivedEvent event) {
			String id = event.getChannel().getId();

			OPERATIONS.keySet().remove(null);
			OPERATIONS.values().remove(null);

			RunningOperation operation = OPERATIONS.get(id);

			if (operation != null) {
				if (operation.getOperation().run(event)) {
					OPERATIONS.remove(id);
				} else {
					operation.getIncreasingTimeout().ifPresent(value -> EXPIRATOR.updateExpire(System.currentTimeMillis() + value, operation));
				}
			}
		}
	};

	public static boolean create(String channelId, String operationName, int startingTimeout, OptionalInt increasingTimeout, InteractiveOperation operation) {
		Objects.requireNonNull(channelId, "channelId");
		Objects.requireNonNull(increasingTimeout, "increasingTimeout");
		Objects.requireNonNull(operation, "operation");

		RunningOperation op = new RunningOperation(increasingTimeout, operation, operationName);

		if (OPERATIONS.containsKey(channelId)) return false;

		OPERATIONS.put(channelId, op);
		EXPIRATOR.letExpire(System.currentTimeMillis() + startingTimeout, op);

		return true;
	}

	public static boolean create(TextChannel channel, String operationName, int startingTimeout, OptionalInt increasingTimeout, InteractiveOperation operation) {
		Objects.requireNonNull(channel, "channel");
		return create(channel.getId(), operationName, startingTimeout, increasingTimeout, operation);
	}

	public static EventListener listener() {
		return LISTENER;
	}
}