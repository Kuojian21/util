// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: greeter.proto

package com.test.rpc.grpc;

public final class Greeter {
  private Greeter() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_test_rpc_grpc_HelloRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_test_rpc_grpc_HelloRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_test_rpc_grpc_HelloReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_test_rpc_grpc_HelloReply_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rgreeter.proto\022\021com.test.rpc.grpc\"\034\n\014He" +
      "lloRequest\022\014\n\004name\030\001 \001(\t\"\035\n\nHelloReply\022\017" +
      "\n\007message\030\001 \001(\t2\340\002\n\010Greeter1\022R\n\016SayHello" +
      "Simple\022\037.com.test.rpc.grpc.HelloRequest\032" +
      "\035.com.test.rpc.grpc.HelloReply\"\000\022T\n\016SayH" +
      "elloServer\022\037.com.test.rpc.grpc.HelloRequ" +
      "est\032\035.com.test.rpc.grpc.HelloReply\"\0000\001\022T" +
      "\n\016SayHelloClient\022\037.com.test.rpc.grpc.Hel" +
      "loRequest\032\035.com.test.rpc.grpc.HelloReply" +
      "\"\000(\001\022T\n\014SayHelloChat\022\037.com.test.rpc.grpc" +
      ".HelloRequest\032\035.com.test.rpc.grpc.HelloR" +
      "eply\"\000(\0010\001B\036\n\021com.test.rpc.grpcB\007Greeter" +
      "P\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_test_rpc_grpc_HelloRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_test_rpc_grpc_HelloRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_test_rpc_grpc_HelloRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_com_test_rpc_grpc_HelloReply_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_test_rpc_grpc_HelloReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_test_rpc_grpc_HelloReply_descriptor,
        new java.lang.String[] { "Message", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
