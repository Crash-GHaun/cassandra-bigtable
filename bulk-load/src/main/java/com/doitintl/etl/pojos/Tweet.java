package com.doitintl.etl.pojos;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * CREATE TABLE tweebeetee.tweets (
 *      id bigint,
 *      user_id bigint,
 *      created_at timestamp,
 *      tweet_text text,
 *      hashtag_entities text,
 *      url_entities text,
 *      favorites_count int,
 *      retweet_count int,
 *      quoted_status_id bigint,
 *      in_reply_to_status_id bigint,
 *      PRIMARY KEY (id)
 * );
 */
@Table(keyspace = "tweebeetee", name = "tweets")
@Data
public class Tweet implements Serializable {
	@PartitionKey
	@Column(name = "id")
	Long id;

	@Column(name = "user_id")
	Long userId;

	@Column(name = "created_at")
	Timestamp createdAt;

	@Column(name = "tweet_text")
	String tweetText;

	@Column(name = "hashtag_entities")
	String hashtagEntities;

	@Column(name = "url_entities")
	String urlEntities;

	@Column(name = "favorites_count")
	int favoritesCount;


	@Column(name = "retweet_count")
	int retweetCount;

	@Column(name = "quoted_status_id")
	Long quotedStatusId;

	@Column(name = "in_reply_to_status_id")
	Long inReplyToStatusId;
}
